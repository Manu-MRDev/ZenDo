package mx.com.virtualhand.zendo.domain


import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.android.gms.tasks.TaskCompletionSource
import mx.com.virtualhand.zendo.data.AuthRepository

class AuthUseCase(private val repository: AuthRepository) {

    fun loginWithGoogle(account: GoogleSignInAccount): Task<AuthResult> {
        return repository.signInWithGoogle(account)  // No crear credential aquí
    }


    fun loginWithGitHub(activity: Activity): Task<AuthResult> {
        return repository.signInWithGitHub(activity)
    }

    fun getCurrentUser() = repository.getCurrentUser()
}
