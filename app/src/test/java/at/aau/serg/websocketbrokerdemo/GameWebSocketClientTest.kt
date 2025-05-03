package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.content.res.AssetManager
import okhttp3.WebSocket
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream
import kotlinx.coroutines.Dispatchers

class GameWebSocketClientTest {

    private lateinit var context: Context
    private lateinit var assetManager: AssetManager

    @BeforeEach
    fun setUp() {
        context = mock(Context::class.java)
        assetManager = mock(AssetManager::class.java)
        `when`(context.assets).thenReturn(assetManager)
    }


    @AfterEach
    fun tearDown() {

    }

    @Test
    fun testLoadServerUrl() {
        // Arrange
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        // Act
        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )

        // Zugriff auf private Property mittels Reflection
        val field = GameWebSocketClient::class.java.getDeclaredField("serverUrl")
        field.isAccessible = true
        val loadedUrl = field.get(client) as String

        // Assert
        Assertions.assertEquals("ws://example.com", loadedUrl)
    }


    @Test
    fun testSendMessage() {
        // Arrange
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        var onConnectedCalled = false

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )

        val mockWebSocket = mock(WebSocket::class.java)
        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        webSocketField.set(client, mockWebSocket)

        // Act
        client.sendMessage("Hello")

        // Assert
        verify(mockWebSocket, times(1)).send("Hello")
    }

    @Test
    fun testSendChatMessage() {
        // Arrange
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )

        val mockWebSocket = mock(WebSocket::class.java)
        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        webSocketField.set(client, mockWebSocket)

        // Act
        client.sendChatMessage("user123", "Hi!")

        // Assert: expected JSON
        val expectedJson = """{"type":"CHAT_MESSAGE","playerId":"user123","message":"Hi!"}"""
        verify(mockWebSocket).send(expectedJson)
    }

    @Test
    fun testRollDice() {
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )
            

        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        val mockedWebSocket = mock(WebSocket::class.java)
        webSocketField.set(client, mockedWebSocket)

        client.rollDice()

        verify(mockedWebSocket, times(1)).send("Roll")
    }

    @Test
    fun testManualRollDice_ValidValue() {
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )

        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        val mockedWebSocket = mock(WebSocket::class.java)
        webSocketField.set(client, mockedWebSocket)

        client.manualRollDice(20)

        verify(mockedWebSocket, times(1)).send("MANUAL_ROLL:20")
    }

    @Test
    fun testManualRollDice_InvalidValue() {
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )

        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        val mockedWebSocket = mock(WebSocket::class.java)
        webSocketField.set(client, mockedWebSocket)

        client.manualRollDice(40) // Invalid value

        verify(mockedWebSocket, never()).send(anyString())
    }

    @Test
    fun testManualRollDice_BoundaryValues() {
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> },
            onPlayerPassedGo = { _ -> },
            onTaxPayment = { _, _, _ -> },
            coroutineDispatcher = Dispatchers.IO
        )

        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        val mockedWebSocket = mock(WebSocket::class.java)
        webSocketField.set(client, mockedWebSocket)

        // Test minimum valid value
        client.manualRollDice(1)
        verify(mockedWebSocket, times(1)).send("MANUAL_ROLL:1")

        // Test maximum valid value
        client.manualRollDice(39)
        verify(mockedWebSocket, times(1)).send("MANUAL_ROLL:39")

        // Test value just below minimum
        client.manualRollDice(0)
        verify(mockedWebSocket, never()).send("MANUAL_ROLL:0")

        // Test value just above maximum
        client.manualRollDice(40)
        verify(mockedWebSocket, never()).send("MANUAL_ROLL:40")
    }
}
