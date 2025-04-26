package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import at.aau.serg.websocketbrokerdemo.GameWebSocketClient
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@Ignore
class PlayboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockWebSocketClient = mock(GameWebSocketClient::class.java)

    @Test
    fun testPlayboardScreenDisplaysPlayers() {
        val players = listOf(
            PlayerMoney(id = "1", name = "Player 1", money = 1500, position = 0),
            PlayerMoney(id = "2", name = "Player 2", money = 1500, position = 0)
        )

        composeTestRule.setContent {
            PlayboardScreen(
                players = players,
                currentPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 5,
                dicePlayerId = "",
                webSocketClient = mockWebSocketClient
            )
        }

        // Check if player names are displayed
        composeTestRule.onNodeWithText("Player 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Player 2").assertIsDisplayed()
    }

    @Test
    fun testPlayboardScreenDisplaysNoPlayersMessage() {
        composeTestRule.setContent {
            PlayboardScreen(
                players = emptyList(),
                currentPlayerId = "",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 5,
                dicePlayerId = "",
                webSocketClient = mockWebSocketClient
            )
        }

        // Check if the "Not enough players connected yet" message is displayed
        composeTestRule.onNodeWithText("Not enough players connected yet").assertIsDisplayed()
    }

    @Test
    fun testRollDiceButtonResetsAfterAnimation() = runTest {
        composeTestRule.setContent {
            PlayboardScreen(
                players = emptyList(),
                currentPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 5,
                dicePlayerId = "",
                webSocketClient = mockWebSocketClient
            )
        }

        composeTestRule.onNodeWithText("Roll Dice").performClick()
        // Fast‑forward the 1s delay in LaunchedEffect
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Roll Dice").assertIsEnabled()
    }
}