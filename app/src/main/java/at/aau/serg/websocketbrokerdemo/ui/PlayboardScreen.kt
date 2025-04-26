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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import kotlinx.coroutines.delay
import at.aau.serg.websocketbrokerdemo.data.properties.Property
import at.aau.serg.websocketbrokerdemo.data.properties.PropertyViewModel
import androidx.compose.foundation.Image
import at.aau.serg.websocketbrokerdemo.data.properties.getDrawableIdFromName
import at.aau.serg.websocketbrokerdemo.GameWebSocketClient


@Composable
fun PlayboardScreen(
    players: List<PlayerMoney>,
    currentPlayerId: String,
    onRollDice: () -> Unit,
    onBackToLobby: () -> Unit,
    diceResult:     Int?,
    dicePlayerId:   String?,
    webSocketClient: GameWebSocketClient
) {
    val context = LocalContext.current
    val propertyViewModel = remember { PropertyViewModel() }
    val properties = remember { propertyViewModel.getProperties(context) }

    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var canBuy by remember { mutableStateOf(false) }
    var openedByClick by remember { mutableStateOf(false) }
    var lastPlayerPosition by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(players, dicePlayerId) {
        val currentPlayer = players.find { it.id == dicePlayerId }
        val newPosition = currentPlayer?.position

        if (newPosition != null && newPosition != lastPlayerPosition) {
            lastPlayerPosition = newPosition
            val landedProperty = properties.find { it.position == newPosition }
            if (landedProperty != null) {
                selectedProperty = landedProperty
                openedByClick = false
                canBuy = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD8F3DC)) // Light green background
    ) {
        // Main content area (70% of screen width)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.9f)
                .padding(end = 16.dp)
        ) {
            Gameboard(
                modifier = Modifier.fillMaxSize(),
                players = players,
                properties = properties,
                onTileClick = { tilePos ->
                    // Find the player who rolled the dice (dicePlayerId)
                    val currentPlayer = players.find { it.id == dicePlayerId }
                    selectedProperty = properties.find { it.position == tilePos }
                    openedByClick = true
                    canBuy = currentPlayer?.position == tilePos
                }
            )
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
            DiceRollingButton(
                text = "Roll Dice",
                color = Color(0xFF3FAF3F),
                onClick = onRollDice,
                diceValue = diceResult,
            )
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
                            isCurrentPlayer = player.id == currentPlayerId,
                            playerIndex = players.indexOf(player)
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
        // Popup für Grundstück
        if (selectedProperty != null) {
            val imageResId = getDrawableIdFromName(selectedProperty!!.image, context)

            AlertDialog(
                onDismissRequest = {
                    selectedProperty = null
                    openedByClick = false
                    canBuy = false
                },
                confirmButton = {
                    if (canBuy) { // <<< Nur wenn er wirklich auf dem Feld steht!
                        Button(onClick = {
                            webSocketClient.sendMessage("BUY_PROPERTY:${selectedProperty?.id}")
                            selectedProperty = null
                            openedByClick = false
                            canBuy = false
                        }) {
                            Text("Buy")
                        }
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        selectedProperty = null
                        openedByClick = false
                        canBuy = false
                    }) {
                        Text("Exit")
                    }
                },
                title = {
                    Text(text = selectedProperty!!.name)
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (imageResId != 0) {
                            Image(
                                painter = painterResource(imageResId),
                                contentDescription = selectedProperty!!.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerMoney,
    isCurrentPlayer: Boolean,
    playerIndex: Int
) {
    val playerColors = listOf(
        Color(0x80FF0000), // Less saturated Red
        Color(0x800000FF), // Less saturated Blue
        Color(0x8000FF00), // Less saturated Green
        Color(0x80FFFF00)  // Less saturated Yellow
    )
    
    val backgroundColor = playerColors[playerIndex].copy(alpha = 0.4f)

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Position: ${player.position}",
                color = Color.White,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
fun DiceRollingButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    diceValue: Int?
) {

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
            onClick()
        },
        modifier = Modifier.height(56.dp).scale(scale).rotate(rotation),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
    ) {
        Text(text, fontSize = 18.sp)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // show the face
    DiceFace(diceValue)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(1000)
            isPressed = false
        }
    }
}

@Composable
fun DiceFace(diceValue: Int?) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = diceValue?.toString() ?: "?",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
