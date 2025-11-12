package mx.com.virtualhand.zendo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import mx.com.virtualhand.zendo.data.AuthRepository
import mx.com.virtualhand.zendo.domain.AuthUseCase
import mx.com.virtualhand.zendo.ui.screens.LoginScreen
import mx.com.virtualhand.zendo.ui.screens.MainScreenWithBottomNav
import mx.com.virtualhand.zendo.ui.theme.ZenDoTheme
import mx.com.virtualhand.zendo.ui.viewmodel.NoteViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.NoteViewModelFactory
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var authUseCase: AuthUseCase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Permite que Jetpack Compose maneje los insets del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val auth = Firebase.auth
        authUseCase = AuthUseCase(AuthRepository(auth))

        setContent {
            var themePreference by rememberSaveable { mutableStateOf("system") }
            var showThemeDialog by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(authUseCase.getCurrentUser() != null) }

            ZenDoTheme(themePreference = themePreference) {
                // ✅ Asegura que todos los Composables respeten status bar y navegación
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    if (isLoggedIn) {
                        // Pequeño retraso para evitar salto visual al iniciar
                        LaunchedEffect(Unit) { delay(150) }

                        val navController = rememberNavController()
                        val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory())
                        val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory())

                        MainScreenWithBottomNav(
                            taskViewModel = taskViewModel,
                            noteViewModel = noteViewModel,
                            navController = navController,
                            onLogout = {
                                authUseCase.logout()
                                isLoggedIn = false
                            },
                            onOpenThemeDialog = { showThemeDialog = true }
                        )

                        if (showThemeDialog) {
                            AlertDialog(
                                onDismissRequest = { showThemeDialog = false },
                                title = { Text("Seleccionar tema") },
                                text = {
                                    Column {
                                        TextButton(onClick = {
                                            themePreference = "system"
                                            showThemeDialog = false
                                        }) { Text("Seguir sistema") }

                                        TextButton(onClick = {
                                            themePreference = "light"
                                            showThemeDialog = false
                                        }) { Text("Modo claro") }

                                        TextButton(onClick = {
                                            themePreference = "dark"
                                            showThemeDialog = false
                                        }) { Text("Modo oscuro") }
                                    }
                                },
                                confirmButton = {}
                            )
                        }

                    } else {
                        LoginScreen(
                            onGoogleLogin = {
                                signInWithGoogle { success ->
                                    if (success) {
                                        window.decorView.requestLayout()
                                        isLoggedIn = true
                                    }
                                }
                            },
                            onGithubLogin = {
                                signInWithGitHub { success ->
                                    if (success) {
                                        window.decorView.requestLayout()
                                        isLoggedIn = true
                                    }
                                }
                            }
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