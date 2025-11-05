package mx.com.virtualhand.zendo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import mx.com.virtualhand.zendo.ui.viewmodel.NoteViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.NoteViewModelFactory
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModelFactory
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


class MainActivity : ComponentActivity() {

    private lateinit var authUseCase: AuthUseCase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth
        authUseCase = AuthUseCase(AuthRepository(auth))

        setContent {
            // Estado para controlar el tema
            var themePreference by rememberSaveable { mutableStateOf("system") }
            // Estado para mostrar el diálogo de selección de tema
            var showThemeDialog by remember { mutableStateOf(false) }

            ZenDoTheme(themePreference = themePreference) {
                var isLoggedIn by remember { mutableStateOf(authUseCase.getCurrentUser() != null) }

                if (isLoggedIn) {
                    val navController = rememberNavController()

                    val taskViewModel: TaskViewModel = viewModel(
                        factory = TaskViewModelFactory()
                    )
                    val noteViewModel: NoteViewModel = viewModel(
                        factory = NoteViewModelFactory()
                    )

                    MainScreenWithBottomNav(
                        taskViewModel = taskViewModel,
                        noteViewModel = noteViewModel,
                        navController = navController,
                        onLogout = {
                            authUseCase.logout()
                            isLoggedIn = false
                        },
                        onOpenThemeDialog = { showThemeDialog = true } // Abrir diálogo desde Configuración
                    )

                    // Diálogo de selección de tema
                    if (showThemeDialog) {
                        AlertDialog(
                            onDismissRequest = { showThemeDialog = false },
                            title = { Text("Seleccionar tema") },
                            text = {
                                Column {
                                    TextButton(onClick = { themePreference = "system"; showThemeDialog = false }) {
                                        Text("Seguir sistema")
                                    }
                                    TextButton(onClick = { themePreference = "light"; showThemeDialog = false }) {
                                        Text("Modo claro")
                                    }
                                    TextButton(onClick = { themePreference = "dark"; showThemeDialog = false }) {
                                        Text("Modo oscuro")
                                    }
                                }
                            },
                            confirmButton = { /* No hace falta botón adicional */ },
                        )
                    }

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
