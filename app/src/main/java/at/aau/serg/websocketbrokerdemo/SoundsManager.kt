package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.myapplication.R

object SoundManager {

    private const val MAX_STREAMS = 4
    private var soundPool: SoundPool? = null
    private val loadedIds = mutableMapOf<GameSound, Int>()

    fun init(context: Context) {
        if (soundPool != null) return         // already initialised

        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
            .also { pool ->
                GameSound.values().forEach { gs ->
                    loadedIds[gs] = pool.load(context, gs.resId, 1)
                }
            }
    }

    fun play(sound: GameSound, volume: Float = 1f, rate: Float = 1f) {
        val pool = soundPool ?: return
        val id   = loadedIds[sound] ?: return
        pool.play(id, volume, volume, 0, 0, rate)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        loadedIds.clear()
    }
}

enum class GameSound(val resId: Int) {
    WIN (R.raw.win_sound),
    DICE(R.raw.dice_roll),
    JAIL(R.raw.jail_sound)
}
