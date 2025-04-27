package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.content.res.AssetManager
import okhttp3.WebSocket
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.mockito.Mockito.*
import android.util.Log
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
            onConnected = {}, // <- einfaches leeres Lambda
            onMessageReceived = { /* Not needed for this test */ },
            onDiceRolled = { _, _ -> }, // Also not needed for this test
            onGameStateReceived = {} //
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
            onConnected = { onConnectedCalled = true },
            onMessageReceived = { /* Not needed for this test */ },
            onDiceRolled = { _, _ -> }, // Also not needed for this test
            onGameStateReceived = {} //
        )


        val webSocketField = GameWebSocketClient::class.java.getDeclaredField("webSocket")
        webSocketField.isAccessible = true
        val mockedWebSocket = mock(WebSocket::class.java)
        webSocketField.set(client, mockedWebSocket)


        client.sendMessage("Hello")


        verify(mockedWebSocket, times(1)).send("Hello")
    }


}