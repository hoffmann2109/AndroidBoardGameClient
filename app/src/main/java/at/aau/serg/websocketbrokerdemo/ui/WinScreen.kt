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


@Composable
fun WinScreen(onTimeout: () -> Unit) {
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
        delay(5000)
        onTimeout()
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 💸 Geldschein-Regen (im Hintergrund)
        repeat(10) {
            FallingMoney()
        }

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn()
        ) {
            Text(
                text = "🎉 Congratulations, you have won! 🎉",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FallingMoney(modifier: Modifier = Modifier) {
    val screenHeight = 800
    val randomX = remember { Random.nextInt(0, 300).dp }
    val duration = remember { Random.nextInt(3000, 6000) }

    val offsetY by rememberInfiniteTransition().animateFloat(
        initialValue = -50f,
        targetValue = screenHeight.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Text(
        text = "💵",
        fontSize = 24.sp,
        modifier = modifier
            .offset(x = randomX, y = offsetY.dp)
    )
}


