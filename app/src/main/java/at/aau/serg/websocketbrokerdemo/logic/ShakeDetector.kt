package at.aau.serg.websocketbrokerdemo.logic

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

@Composable
fun ShakeDetector(
    shakingThreshold: Float = 15f,
    onShake: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val lastShakeTime = remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val (xCoordinate, yCoordinate, zCoordinate) = event.values
                val magnitude = sqrt(xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate)
                if (magnitude > shakingThreshold) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime.longValue > 1000) {
                        lastShakeTime.longValue = now
                        onShake()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d("SensorListener", "The Sensor accuracy changed: $sensor, New Accuracy level: $accuracy")
            }
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}
