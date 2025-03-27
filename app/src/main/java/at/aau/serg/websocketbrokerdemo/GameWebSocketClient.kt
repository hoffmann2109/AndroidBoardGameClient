package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.Properties

class GameWebSocketClient(
    context: Context,
    private val onConnected: () -> Unit
) {
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket


    private val serverUrl: String = loadServerUrl(context) ?: "ws://10.0.2.2:8080/monopoly"


    private val request: Request = Request.Builder()
        .url(serverUrl)
        .build()

    init {

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected to server")
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
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket.send(message)
    }

    fun close() {
        webSocket.close(1000, "Goodbye!")
    }

    private fun loadServerUrl(context: Context): String? {
        val properties = Properties()
        context.assets.open("config.properties").use { input ->
            properties.load(input)
        }
        return properties.getProperty("server.url")
    }
}
