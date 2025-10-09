package mx.com.virtualhand.zendo.data

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.android.gms.tasks.Task

class AuthRepository(
    private val auth: FirebaseAuth
) {

    // Login con Google
    fun signInWithGoogle(account: GoogleSignInAccount): Task<AuthResult> {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        return auth.signInWithCredential(credential)
    }

    // Login con GitHub
    fun signInWithGitHub(activity: Activity, token: String? = null): Task<AuthResult> {
        val provider = OAuthProvider.newBuilder("github.com")

        // Si ya tienes token de GitHub (ej: desde OAuth), puedes agregarlo
        token?.let {
            provider.addCustomParameter("access_token", it)
        }

        return auth.startActivityForSignInWithProvider(activity, provider.build())
    }

    // Revisar si ya hay usuario logueado
    fun getCurrentUser() = auth.currentUser

    fun logout() {
        auth.signOut()
    }
}
