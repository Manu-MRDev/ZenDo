package mx.com.virtualhand.zendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.com.virtualhand.zendo.data.AuthRepository
import mx.com.virtualhand.zendo.domain.AuthUseCase
import mx.com.virtualhand.zendo.ui.screens.LoginScreen
import mx.com.virtualhand.zendo.ui.theme.ZenDoTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authUseCase: AuthUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Firebase Auth y la capa de domain
        auth = Firebase.auth
        val repository = AuthRepository(auth)
        authUseCase = AuthUseCase(repository)

        setContent {
            ZenDoTheme {
                Scaffold(modifier = androidx.compose.ui.Modifier.fillMaxSize()) { _ ->
                    Surface {
                        LoginScreen(
                            onGoogleLogin = { signInWithGoogle() },
                            onGithubLogin = { signInWithGitHub() }
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
                    .addOnSuccessListener {
                        println("✅ Google login: ${it.user?.email}")
                    }
                    .addOnFailureListener {
                        println("❌ Error Google: ${it.message}")
                    }
            }
        } catch (e: Exception) {
            println("⚠️ Google Sign-In cancelado o fallido: ${e.message}")
        }
    }

    private fun signInWithGoogle() {
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
    private fun signInWithGitHub() {
        authUseCase.loginWithGitHub(this)
            .addOnSuccessListener {
                println("✅ GitHub login: ${it.user?.email}")
            }
            .addOnFailureListener {
                println("❌ Error GitHub: ${it.message}")
            }
    }
}
