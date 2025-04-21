package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import kotlinx.coroutines.delay

@Composable
fun PlayboardScreen(
    players: List<PlayerMoney>,
    currentPlayerId: String,
    onRollDice: () -> Unit,
    onBackToLobby: () -> Unit
) {
    var diceResult by remember { mutableStateOf("?") }
    diceResult = parseDiceResult("Player 2 rolled 5")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD8F3DC)) // Light green background
    ) {
        // Main content area (70% of screen width)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp)
        ) {
            // This is where the game board will be displayed
            // For now, it's empty
        }

        // Dice info column on the left (10% of screen width)
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp, top = 16.dp)
                .fillMaxWidth(0.1f)
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DiceRollingButton("Roll Dice", Color(0xFF3FAF3F), onRollDice, diceResult)
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

fun parseDiceResult(newContent: String): String {
    val diceRegex = "rolled (\\d+)".toRegex()
    val matchResult = diceRegex.find(newContent)
    return if (matchResult != null){
        matchResult.groupValues[1]
    } else {
        "?" // Displays ? if no dice result was found
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

@Composable
fun DiceRollingButton(text: String, color: Color, onClick: () -> Unit, diceResult: String) {
    var isPressed by remember { mutableStateOf(false) }
    var rotateAngle by remember { mutableFloatStateOf(0f) }

    val rotation by animateFloatAsState(
        targetValue = rotateAngle,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    val scale by animateFloatAsState(if (isPressed) 1.1f else 1f, animationSpec = tween(150))
    val buttonColor by animateColorAsState(
        targetValue = if (isPressed) color.copy(alpha = 0.7f) else color,
        animationSpec = tween(durationMillis = 150)
    )

    Button(
        onClick = {
            isPressed = true
            rotateAngle += 720f
            onClick() // Hier wird diceResult im Log aktualisiert
        },
        modifier = Modifier.height(56.dp).scale(scale).rotate(rotation),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(text, fontSize = 18.sp)
    }

    // Anzeige der geworfenen Zahl
    DiceFace(diceResult)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(1000)
            isPressed = false
        }
    }
}

@Composable
fun DiceFace(diceValue: String) {
    Box(
        modifier = Modifier.size(100.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = diceValue,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}