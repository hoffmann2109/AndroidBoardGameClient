package at.aau.serg.websocketbrokerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {

    private val webSocketClient = GameWebSocketClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonopolyWebSocketApp()
        }
    }

    @Composable
    fun MonopolyWebSocketApp() {
        var message by remember { mutableStateOf("") }
        var log by remember { mutableStateOf("Logs:\n") }

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
                    webSocketClient.connect {
                        log += "Connected to server\n"
                    }
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

    @Preview(showBackground = true)
    @Composable
    fun PreviewApp() {
        MonopolyWebSocketApp()
    }
}
