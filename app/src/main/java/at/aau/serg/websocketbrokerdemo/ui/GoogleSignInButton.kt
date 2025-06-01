package at.aau.serg.websocketbrokerdemo.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import at.aau.serg.websocketbrokerdemo.auth.AuthManager
import com.example.myapplication.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun GoogleSignInButton(onSuccess: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            Firebase.auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        // Firestore: Create if not exists
                        AuthManager.createUserIfNotExists(user , context) {
                            onSuccess()
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("GoogleSignIn", "Firebase Auth failed", it)
                }

        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google sign-in failed", e)
        }
    }

    val signInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    Button(onClick = {
        launcher.launch(signInClient.signInIntent)
    }) {
        Text("Mit Google anmelden")
    }
}

