package mx.com.virtualhand.zendo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.com.virtualhand.zendo.data.AuthRepository
import mx.com.virtualhand.zendo.domain.AuthUseCase
import mx.com.virtualhand.zendo.ui.screens.LoginScreen
import mx.com.virtualhand.zendo.ui.screens.MainScreenWithBottomNav
import mx.com.virtualhand.zendo.ui.theme.ZenDoTheme
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var authUseCase: AuthUseCase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        authUseCase = AuthUseCase(AuthRepository(auth))

        setContent {
            ZenDoTheme {
                var isLoggedIn by remember { mutableStateOf(authUseCase.getCurrentUser() != null) }

                if (isLoggedIn) {
                    val taskViewModel: TaskViewModel = viewModel(
                        factory = TaskViewModelFactory(applicationContext)
                    )

                    val navController = rememberNavController() // ✅ Creamos NavController aquí

                    MainScreenWithBottomNav(
                        taskViewModel = taskViewModel
                    )

                } else {
                    LoginScreen(
                        onGoogleLogin = { signInWithGoogle { success -> isLoggedIn = success } },
                        onGithubLogin = { signInWithGitHub { success -> isLoggedIn = success } }
                    )
                }
            }
        }
    }

    // -------------------------------
    // 🔹 Login con Google
    // -------------------------------
    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            account?.let {
                authUseCase.loginWithGoogle(it)
                    .addOnSuccessListener { googleLoginCallback(true) }
                    .addOnFailureListener { googleLoginCallback(false) }
            } ?: googleLoginCallback(false)
        } catch (e: Exception) {
            googleLoginCallback(false)
        }
    }

    private var googleLoginCallback: (Boolean) -> Unit = {}

    private fun signInWithGoogle(onResult: (Boolean) -> Unit) {
        googleLoginCallback = onResult
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, gso)
        googleLauncher.launch(googleClient.signInIntent)
    }

    // -------------------------------
    // 🔹 Login con GitHub
    // -------------------------------
    private fun signInWithGitHub(onResult: (Boolean) -> Unit) {
        authUseCase.loginWithGitHub(this)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
