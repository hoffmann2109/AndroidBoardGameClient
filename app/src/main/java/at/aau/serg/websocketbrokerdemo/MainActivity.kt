package at.aau.serg.websocketbrokerdemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

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
                onConnected = {
                    log += "Connected to server\n"
                },
                onMessageReceived = { receivedMessage ->
                    log += "Received: $receivedMessage\n"
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Monopoly WebSocket", style = MaterialTheme.typography.bodyMedium)
            TextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Enter your message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    webSocketClient.connect()
                }) {
                    Text("Connect")
                }

                Button(onClick = {
                    webSocketClient.close()
                    log += "Disconnected from server\n"
                }) {
                    Text("Disconnect")
                }
            }

            Button(
                onClick = {
                    if (message.isNotEmpty()) {
                        webSocketClient.sendMessage(message)
                        log += "Sent: $message\n"
                        message = ""
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Send Message")
            }

            // Roll button
            Button(
                onClick = {
                    webSocketClient.sendMessage("Roll")
                    log += "Sent: Roll command\n"
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Roll Dice")
            }

            // Logout button
            Button(
                onClick = {
                    auth.signOut()  // Sign out user
                    val intent = Intent(context, AuthActivity::class.java)
                    context.startActivity(intent)
                    (context as? android.app.Activity)?.finish() // Close MainActivity
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Logout")
            }

            Text(
                text = log,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            )
        }
    }
}
