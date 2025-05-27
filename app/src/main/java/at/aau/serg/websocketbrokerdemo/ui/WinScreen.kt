package at.aau.serg.websocketbrokerdemo.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.background
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer


@Composable
fun WinScreen(onTimeout: () -> Unit) {
    val visible = remember { mutableStateOf(false) }

    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        visible.value = true
        delay(5000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3), // Dunkelblau oben
                        Color(0xFFBBDEFB)  // Hellblau unten
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // 💸 Geldschein-Regen (im Hintergrund)
        repeat(50) {
            FallingSymbol()
        }

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn()
        ) {
            Text(
                modifier = Modifier.scale(scale),
                text = "🎉 Congratulations, you have won! 🎉",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
fun FallingSymbol() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val randomX = remember { Random.nextInt(0, screenWidth).dp }
    val duration = remember { Random.nextInt(3000, 6000) }

    // Fallbewegung (Y)
    val offsetY by rememberInfiniteTransition().animateFloat(
        initialValue = -50f,
        targetValue = screenHeight.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // 🎵 Tanzbewegung (Rotation)
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 🎲 Zufälliges Symbol
    val symbols = listOf("💵", "🏠", "🎲", "🏦", "🤑")
    val symbol = remember { symbols.random() }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = symbol,
            fontSize = 28.sp,
            modifier = Modifier
                .absoluteOffset(x = randomX, y = offsetY.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
    }
}