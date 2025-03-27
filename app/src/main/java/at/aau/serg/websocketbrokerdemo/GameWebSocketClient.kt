package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.Properties

class GameWebSocketClient(
    private val context: Context,
    private val onConnected: () -> Unit
) {
    private val client = OkHttpClient()
    // Use a nullable WebSocket so we can check if it's already connected
    private var webSocket: WebSocket? = null

    // Load the server URL from the config.properties file in the assets folder.
    private val serverUrl: String = loadServerUrl(context)

    private val request: Request = Request.Builder()
        .url(serverUrl)
        .build()

    /**
     * Connects to the server if not already connected.
     */
    fun connect() {
        if (webSocket == null) {
            initWebSocket()
        } else {
            Log.d("WebSocket", "Already connected or connection already exists.")
        }
    }

    // Initializes the WebSocket connection.
    private fun initWebSocket() {
        webSocket = client.newWebSocket(request, createListener())
    }

    // Creates a WebSocketListener that handles connection events.
    private fun createListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "Connected to server with response: ${response.message}")
            onConnected()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "Received: $text")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocket", "Received bytes: ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Closing: $reason")
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Closed: $reason")
            // Reset the WebSocket variable when closed.
            this@GameWebSocketClient.webSocket = null
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "Error: ${t.message}", t)
            response?.let {
                Log.e("WebSocket", "Response: ${it.code} - ${it.message}")
            }
            // Optionally, reset the webSocket so that connect() can try again.
            this@GameWebSocketClient.webSocket = null
        }
    }


    fun sendMessage(message: String) {
        webSocket?.send(message)
    }


    fun close() {
        webSocket?.close(1000, "Goodbye!")
        // Reset the connection.
        webSocket = null
    }


    private fun loadServerUrl(context: Context): String {
        val properties = Properties()
        context.assets.open("config.properties").use { input ->
            properties.load(input)
        }
        return properties.getProperty("server.url")
    }
}
