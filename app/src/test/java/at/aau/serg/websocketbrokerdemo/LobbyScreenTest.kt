package at.aau.serg.websocketbrokerdemo

import androidx.test.ext.junit.runners.AndroidJUnit4
import at.aau.serg.websocketbrokerdemo.ui.parseDiceResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LobbyScreenTest {

    @BeforeEach
    fun setup(){

    }

    @Test
    fun diceResultIsParsedCorrectly(){
        val input1 = "Player rolled 4"
        val expected1 = "4"
        assertEquals(expected1, parseDiceResult(input1))

        val input2 = "Game update: Player rolled 6 and moved forward"
        val expected2 = "6"
        assertEquals(expected2, parseDiceResult(input2))

        val input3 = "No roll happened this round"
        val expected3 = "?"
        assertEquals(expected3, parseDiceResult(input3))
    }

    @Test
    fun NoDiceResultFoundDisplaysQuestionmark(){
        val input1 = "Hello World!"
        val expected1 = "?"
        assertEquals(expected1, parseDiceResult(input1))
        val input2 = ""
        val expected2 = "?"
        assertEquals(expected2, parseDiceResult(input2))
    }



}