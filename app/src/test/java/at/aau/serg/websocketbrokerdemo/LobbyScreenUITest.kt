package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.content.res.AssetManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.aau.serg.websocketbrokerdemo.ui.parseDiceResult
import okhttp3.WebSocket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LobbyScreenUITest {

    @BeforeEach
    fun setup(){

    }

    @Test
    fun diceResultIsParsedCorrectly(){
        // Case 1: Valid dice result is present
        val input1 = "Player rolled 4"
        val expected1 = "4"
        assertEquals(expected1, parseDiceResult(input1))

        // Case 2: Valid dice result is present in a longer message
        val input2 = "Game update: Player rolled 6 and moved forward"
        val expected2 = "6"
        assertEquals(expected2, parseDiceResult(input2))

        // Case 3: No dice result present, should return "?"
        val input3 = "No roll happened this round"
        val expected3 = "?"
        assertEquals(expected3, parseDiceResult(input3))
    }

}