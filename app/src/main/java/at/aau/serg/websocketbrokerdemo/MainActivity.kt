package at.aau.serg.websocketbrokerdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import at.aau.serg.websocketbrokerdemo.ui.LobbyScreen
import androidx.navigation.compose.*
import at.aau.serg.websocketbrokerdemo.data.PlayerProfile
import at.aau.serg.websocketbrokerdemo.data.FirestoreManager
import at.aau.serg.websocketbrokerdemo.ui.UserProfileScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import at.aau.serg.websocketbrokerdemo.ui.PlayboardScreen
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney

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
        var playerProfile by remember { mutableStateOf<PlayerProfile?>(null) }
        var playerMoneyList by remember { mutableStateOf<List<PlayerMoney>>(emptyList()) }

        // Firebase Auth instance
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        LaunchedEffect(userId) {
            if (userId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    playerProfile = FirestoreManager.getUserProfile(userId)
                }
            }
        }

        // Create websocket client
        val webSocketClient = remember {
            GameWebSocketClient(
                context = context,
                onConnected = { log += "Connected to server\n" },
                onMessageReceived = { receivedMessage -> log += "Received: $receivedMessage\n" },
                onGameStateReceived = { players -> playerMoneyList = players }
            )
        }

        val navController = rememberNavController()

        NavHost(navController, startDestination = "lobby") {
            composable("lobby") {
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
                    },
                    onProfileClick = { navController.navigate("profile") },
                    onJoinGame = { navController.navigate("playerInfo") }
                )
            }
            composable("profile") {
                UserProfileScreen(
                    playerProfile = playerProfile,
                    onNameChange = { newName ->
                        CoroutineScope(Dispatchers.IO).launch {
                            userId?.let { FirestoreManager.updateUserProfileName(it, newName) }
                            playerProfile = playerProfile?.copy(name = newName)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("playerInfo") {
                PlayboardScreen(
                    players = playerMoneyList,
                    currentPlayerId = userId ?: "",
                    onBackToLobby = { navController.navigate("lobby") }
                )
            }
        }
    }
}
