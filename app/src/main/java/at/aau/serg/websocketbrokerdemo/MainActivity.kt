package at.aau.serg.websocketbrokerdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import at.aau.serg.websocketbrokerdemo.ui.theme.LobbyScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MonopolyWebSocketApp() }
    }

    @Composable
    fun MonopolyWebSocketApp() {
        val context = LocalContext.current
        var message by remember { mutableStateOf("") }
        var log by remember { mutableStateOf("Logs:\n") }

        // Firebase Auth instance
        val auth = FirebaseAuth.getInstance()

        // Create websocket client
        val webSocketClient = remember {
            GameWebSocketClient(
                context = context,
                onConnected = { log += "Connected to server\n" },
                onMessageReceived = { receivedMessage -> log += "Received: $receivedMessage\n" }
            )
        }

        // Lobby UI mit allen Funktionen verbinden
        LobbyScreen(
            message = message,
            log = log,
            onMessageChange = { message = it },
            onConnect = { webSocketClient.connect() },
            onDisconnect = { webSocketClient.close(); log += "Disconnected from server\n" },
            onSendMessage = {
                if (message.isNotEmpty()) {
                    webSocketClient.sendMessage(message)
                    log += "Sent: $message\n"
                    message = ""
                }
            },
            onRollDice = { webSocketClient.sendMessage("Roll"); log += "Sent: Roll command\n" },
            onLogout = {
                auth.signOut()
                val intent = Intent(context, AuthActivity::class.java)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            }
        )
    }
}
