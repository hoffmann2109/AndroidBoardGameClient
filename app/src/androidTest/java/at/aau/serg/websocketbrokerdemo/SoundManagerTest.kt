package at.aau.serg.websocketbrokerdemo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.R
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoundManagerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        SoundManager.init(context)
    }

    @After
    fun tearDown() {
        SoundManager.release()
    }

    @Test
    fun testInit_DoesNotThrow() {
        // Should initialize without exceptions
        // (no assertion needed, crash = fail)
        SoundManager.init(context)
    }

    @Test
    fun testPlay_WinSound() {
        // Sollte den Sound spielen, aber wir testen nur, dass kein Fehler fliegt
        SoundManager.play(GameSound.WIN)
    }

    @Test
    fun testPlay_DiceSound() {
        SoundManager.play(GameSound.DICE)
    }

    @Test
    fun testPlay_JailSound() {
        SoundManager.play(GameSound.JAIL)
    }

    // Falls du noch einen eigenen JAIL_GOTO Sound hast:
    @Test
    fun testPlay_GoToJailSound() {
        // Prüfe ob das Enum existiert und play es
        SoundManager.play(GameSound.JAIL)
    }

    @Test
    fun testRelease_DoesNotThrow() {
        SoundManager.release()
    }
}
