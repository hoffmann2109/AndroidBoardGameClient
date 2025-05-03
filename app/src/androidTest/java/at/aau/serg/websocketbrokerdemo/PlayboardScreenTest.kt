package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.aau.serg.websocketbrokerdemo.data.ChatEntry
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

    private val emptyChat = emptyList<ChatEntry>()

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
                localPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 5,
                dicePlayerId = "",
                webSocketClient = mockWebSocketClient,
                chatMessages = emptyChat,
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
            )
        }

        composeTestRule.onNodeWithText("Player 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Player 2").assertIsDisplayed()
    }

    @Test
    fun testPlayboardScreenDisplaysNoPlayersMessage() {
        composeTestRule.setContent {
            PlayboardScreen(
                players = emptyList(),
                currentPlayerId = "",
                localPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 5,
                dicePlayerId = "",
                webSocketClient = mockWebSocketClient,
                chatMessages = emptyChat,
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
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
                localPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 5,
                dicePlayerId = "",
                webSocketClient = mockWebSocketClient,
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                chatMessages = emptyChat,
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
            )
        }

        composeTestRule.onNodeWithText("Roll Dice").performClick()
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Roll Dice").assertIsEnabled()
    }

    @Test
    fun testPlayboardScreenDisplaysNoPlayersMessageWithNewParameters() {
        composeTestRule.setContent {
            PlayboardScreen(
                players = emptyList(),
                currentPlayerId = "",
                localPlayerId = "",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = null,
                dicePlayerId = null,
                webSocketClient = mockWebSocketClient,
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                chatMessages = emptyChat,
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
            )
        }

        // Check if the "Not enough players connected yet" message is displayed
        composeTestRule.onNodeWithText("Not enough players connected yet").assertIsDisplayed()
    }

    @Test
    fun testPlayboardScreenDisplaysNoPlayersMessageWithNewParametersAgain() {
        composeTestRule.setContent {
            PlayboardScreen(
                players = emptyList(),
                currentPlayerId = "",
                localPlayerId = "",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = null,
                dicePlayerId = null,
                webSocketClient = mockWebSocketClient,
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                chatMessages = emptyChat,
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
            )
        }

        // Check if the "Not enough players connected yet" message is displayed
        composeTestRule.onNodeWithText("Not enough players connected yet").assertIsDisplayed()
    }

    @Test
    fun testChatToggleOpensAndClosesChatOverlay() {
        val players = listOf(
            PlayerMoney(id = "1", name = "Player 1", money = 1500, position = 0)
        )

        composeTestRule.setContent {
            PlayboardScreen(
                players = players,
                currentPlayerId = "1",
                localPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 2,
                dicePlayerId = "1",
                webSocketClient = mockWebSocketClient,
                chatMessages = listOf(),
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
            )
        }

        // Öffne Chat
        composeTestRule.onNodeWithText("Open Chat").performClick()
        composeTestRule.onNodeWithText("Send").assertIsDisplayed()

        // Schließe Chat
        composeTestRule.onNodeWithText("Close Chat").performClick()
        composeTestRule.onNodeWithText("Open Chat").assertIsDisplayed()
    }

    @Test
    fun testChatMessageDisplayAndInput() {
        val messages = listOf(
            ChatEntry(senderId = "1", message = "Hello from me", senderName = "Alex"),
            ChatEntry(senderId = "2", message = "Hello from another", senderName = "Alex2")
        )

        composeTestRule.setContent {
            PlayboardScreen(
                players = listOf(PlayerMoney("1", "Me", 1500, 0)),
                currentPlayerId = "1",
                localPlayerId = "1",
                onRollDice = {},
                onBackToLobby = {},
                diceResult = 1,
                dicePlayerId = "1",
                webSocketClient = mockWebSocketClient,
                chatMessages = messages,
                showPassedGoAlert = false,
                passedGoPlayerName = "",
                showTaxPaymentAlert = false,
                taxPaymentPlayerName = "",
                taxPaymentAmount = 0,
                taxPaymentType = ""
            )
        }

        // Öffne Chat
        composeTestRule.onNodeWithText("Open Chat").performClick()

        // Nachrichten sichtbar
        composeTestRule.onNodeWithText("Hello from me").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello from another").assertIsDisplayed()
    }

}
