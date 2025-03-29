package at.aau.serg.websocketbrokerdemo

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.shadow
import com.example.myapplication.R

@Composable
fun StartScreen(onEnterClick: () -> Unit) {
    val context = LocalContext.current
    val videoView = remember {
        VideoView(context).apply {
            setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.video_startseite}"))
            setOnPreparedListener {
                it.isLooping = true
                start()
                scaleX = 1.5f
                scaleY = 1.2f
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { videoView })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MONOPOLY",
                color = Color.White,
                fontSize = 48.sp,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.shadow(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onEnterClick() },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06292))
            ) {
                Text("Enter Game", color = Color.White)
            }
        }
    }
}
