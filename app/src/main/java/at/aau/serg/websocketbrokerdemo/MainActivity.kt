package at.aau.serg.websocketbrokerdemo

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

        // Erstelle den WebSocketClient, wobei die Verbindung beim Init aufgebaut wird.
        val webSocketClient = remember {
            GameWebSocketClient(context) {
                log += "Connected to server\n"
            }
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
                    // Da die Verbindung bereits beim Erstellen aufgebaut wird,
                    // können wir hier nur eine Info anzeigen.
                    log += "WebSocket already connected\n"
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
