package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.content.res.AssetManager
import okhttp3.WebSocket
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney

class GameWebSocketClientTest {

    private lateinit var context: Context
    private lateinit var assetManager: AssetManager

    @BeforeEach
    fun setUp() {
        context = mock(Context::class.java)
        assetManager = mock(AssetManager::class.java)
        `when`(context.assets).thenReturn(assetManager)
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
            onChatMessageReceived = { _, _ -> }
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

        val client = GameWebSocketClient(
            context,
            onConnected = {},
            onMessageReceived = {},
            onDiceRolled = { _, _ -> },
            onGameStateReceived = {},
            onPlayerTurn = {},
            onChatMessageReceived = { _, _ -> }
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
            onChatMessageReceived = { _, _ -> }
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

}
