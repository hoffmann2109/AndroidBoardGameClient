package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney

@Composable
fun PlayboardScreen(
    players: List<PlayerMoney>,
    currentPlayerId: String,
    onBackToLobby: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD8F3DC)) // Light green background
    ) {
        // Main content area (80% of screen width)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp)
        ) {
            // This is where the game board will be displayed
            // For now, it's empty
        }

        // Player info column on the right (20% of screen width)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(0.2f)
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (players.isEmpty()) {
                Text(
                    text = "Not enough players connected yet",
                    color = Color.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(players.take(4)) { player ->
                        PlayerCard(
                            player = player,
                            isCurrentPlayer = player.id == currentPlayerId
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBackToLobby,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0074cc)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Back to Lobby", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerMoney,
    isCurrentPlayer: Boolean
) {
    val backgroundColor = if (isCurrentPlayer) Color(0x4000FF00) else Color(0x40000000)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = player.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ID: ${player.id}",
                color = Color.White,
                fontSize = 8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "EUR",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${player.money}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}