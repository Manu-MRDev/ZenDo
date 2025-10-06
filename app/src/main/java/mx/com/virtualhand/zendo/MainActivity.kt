package mx.com.virtualhand.zendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.com.virtualhand.zendo.data.AuthRepository
import mx.com.virtualhand.zendo.domain.AuthUseCase
import mx.com.virtualhand.zendo.ui.screens.LoginScreen
import mx.com.virtualhand.zendo.ui.screens.MainScreen
import mx.com.virtualhand.zendo.ui.theme.ZenDoTheme
import mx.com.virtualhand.zendo.domain.TaskUseCase
import mx.com.virtualhand.zendo.data.TaskRepository

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authUseCase: AuthUseCase
    private lateinit var taskUseCase: TaskUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Firebase Auth y repositorios
        auth = Firebase.auth
        val authRepository = AuthRepository(auth)
        authUseCase = AuthUseCase(authRepository)

        val taskRepository = TaskRepository()
        taskUseCase = TaskUseCase(taskRepository)

        setContent {
            ZenDoTheme {
                var isLoggedIn by remember { mutableStateOf(authUseCase.getCurrentUser() != null) }

                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    if (isLoggedIn) {
                        // Pantalla principal estilo ZenDo
                        MainScreen(taskUseCase)
                    } else {
                        // Pantalla de login
                        LoginScreen(
                            onGoogleLogin = { signInWithGoogle { success -> isLoggedIn = success } },
                            onGithubLogin = { signInWithGitHub { success -> isLoggedIn = success } }
                        )
                    }
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

