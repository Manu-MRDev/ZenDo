package mx.com.virtualhand.zendo.domain

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import mx.com.virtualhand.zendo.data.AuthRepository

class AuthUseCase(private val repository: AuthRepository) {

    // Login con Google
    fun loginWithGoogle(account: GoogleSignInAccount): Task<AuthResult> {
        return repository.signInWithGoogle(account)
    }

    // Login con GitHub
    fun loginWithGitHub(activity: Activity): Task<AuthResult> {
        return repository.signInWithGitHub(activity)
    }

    // Usuario actual
    fun getCurrentUser() = repository.getCurrentUser()
}
