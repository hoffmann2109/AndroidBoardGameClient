package at.aau.serg.websocketbrokerdemo
import android.content.Context
import android.media.MediaPlayer

object SoundManager {

    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, soundResId: Int) {
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        mediaPlayer?.start()
    }

    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
