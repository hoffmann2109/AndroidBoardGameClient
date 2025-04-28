package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import okhttp3.WebSocket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.mockito.Mockito.*
import io.mockk.every
import io.mockk.mockkStatic
import java.io.ByteArrayInputStream

class GameWebSocketClientTest {
    private lateinit var context: Context
    private lateinit var assetManager: AssetManager

    @BeforeEach
    fun setUp() {
        // Erstelle Mocks für Context und AssetManager
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        context = mock(Context::class.java)
        assetManager = mock(AssetManager::class.java)
        `when`(context.assets).thenReturn(assetManager)
    }

    @AfterEach
    fun tearDown() {

    }

    @Test
    fun testLoadServerUrl() {
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        val client = GameWebSocketClient(
            context,
            onConnected        = { /* unused */ },
            onMessageReceived  = { /* unused */ },
            onDiceRolled       = { _, _ -> /* unused */ },
            onGameStateReceived= { /* unused */ },
            onPlayerTurn       = { _ -> /* unused */ }
        )

        val field = GameWebSocketClient::class.java.getDeclaredField("serverUrl")
        field.isAccessible = true
        val loadedUrl = field.get(client) as String


        assertEquals("ws://example.com", loadedUrl)
    }


    @Test
    fun testSendMessage() {
        val propertiesContent = "server.url=ws://example.com"
        val inputStream = ByteArrayInputStream(propertiesContent.toByteArray())
        `when`(assetManager.open("config.properties")).thenReturn(inputStream)

        var onConnectedCalled = false

        val client = GameWebSocketClient(
            context,
            onConnected        = { onConnectedCalled = true },
            onMessageReceived  = { /* unused */ },
            onDiceRolled       = { _, _ -> /* unused */ },
            onGameStateReceived= { /* unused */ },
            onPlayerTurn       = { _ -> /* unused */ }
        )

        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        val mockedWebSocket = mock(WebSocket::class.java)
        webSocketField.set(client, mockedWebSocket)


        client.sendMessage("Hello")


        verify(mockedWebSocket, times(1)).send("Hello")
    }
}
