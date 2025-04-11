package at.aau.serg.websocketbrokerdemo.ui

import android.content.Context
import android.content.Intent
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import at.aau.serg.websocketbrokerdemo.MainActivity
import at.aau.serg.websocketbrokerdemo.data.FirestoreManager
import at.aau.serg.websocketbrokerdemo.data.PlayerProfile
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()

    // Funktion zum Setzen der Fehlermeldung
    val setErrorMessage: (String) -> Unit = { message ->
        errorMessage = message
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // Passwort im Maskierungsmodus
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Fehlermeldung anzeigen, wenn gesetzt
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = { registerUser(auth, email, password, context, setErrorMessage) }) {
            Text("Register")
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
    }
}

internal fun registerUser(auth: FirebaseAuth, email: String, password: String, context: Context, setErrorMessage: (String) -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            val userId = authResult.user?.uid
            if (userId != null) {
                val profile = PlayerProfile(name = "user${userId.takeLast(4)}")
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        FirestoreManager.saveUserProfile(userId, profile)
                        (context as? Activity)?.runOnUiThread {
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as? Activity)?.finish()
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterScreen", "Error saving profile: ${e.message}")
                        (context as? Activity)?.runOnUiThread {
                            setErrorMessage("Failed to save profile.")
                        }
                    }
                }
            } else {
                Log.e("RegisterScreen", "Failed to get user ID.")
                (context as? Activity)?.runOnUiThread {
                    setErrorMessage("Failed to get user ID.")
                }
            }
        }
        .addOnFailureListener { error ->
            // Fehlerbehandlung
            (context as? Activity)?.runOnUiThread {
                val errorMessage = when (error) {
                    is FirebaseAuthException -> {
                        // Fehlerbehandlung basierend auf dem ErrorCode
                        when (error.errorCode) {
                            "ERROR_WEAK_PASSWORD" -> "The password is too weak. Please choose a stronger password."
                            "ERROR_INVALID_EMAIL" -> "The email address is invalid."
                            "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use. Try another one."
                            else -> "Registration failed: ${error.message ?: "Unknown error."}"
                        }
                    }
                    is FirebaseException -> {
                        // Allgemeiner FirebaseException Fehler
                        "An error occurred. Please try again later."
                    }
                    else -> {
                        // Fallback, falls der Fehler ein unerwarteter Typ ist
                        "Unknown error: ${error.message ?: "Unknown error."}"
                    }
                }

                // Den Fehlerstatus setzen, um ihn im UI anzuzeigen
                setErrorMessage(errorMessage)
            }
        }
}
