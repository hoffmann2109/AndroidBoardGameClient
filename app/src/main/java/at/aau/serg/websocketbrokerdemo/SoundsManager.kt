package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.media.MediaPlayer
import com.example.myapplication.R

object SoundManager {

    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, sound: GameSound) {
        stopSound() // saubere Ressourcenfreigabe

        mediaPlayer = MediaPlayer.create(context, sound.resId)
        mediaPlayer?.setOnCompletionListener {
            stopSound()
        }
        mediaPlayer?.start()
    }

    fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }
}

enum class GameSound(val resId: Int) {
    WIN(R.raw.win_sound),
    DICE(R.raw.dice_roll),

}
