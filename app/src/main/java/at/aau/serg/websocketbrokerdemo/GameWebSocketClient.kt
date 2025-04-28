package at.aau.serg.websocketbrokerdemo

import android.content.Context
import android.util.Log
import at.aau.serg.websocketbrokerdemo.data.DiceRollMessage
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import java.util.Properties

class GameWebSocketClient(
    private val context: Context,
    private val onConnected: () -> Unit,
    private var onMessageReceived: (String) -> Unit,
    private val onDiceRolled: (playerId: String, value: Int) -> Unit,
    private val onGameStateReceived: (List<PlayerMoney>) -> Unit
) {
    private val client = OkHttpClient()
    // Use a nullable WebSocket so we can check if it's already connected
    private var webSocket: WebSocket? = null
    private val gson = Gson()

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

            if (text.contains("PROPERTY_BOUGHT")) {
                propertyBoughtListener?.invoke(text)
            }
            // Check if this is a game state message
            if (text.startsWith("GAME_STATE:")) {
                try {
                    val jsonData = text.substring("GAME_STATE:".length)
                    val type = object : TypeToken<List<PlayerMoney>>() {}.type
                    val players = gson.fromJson<List<PlayerMoney>>(jsonData, type)
                    onGameStateReceived(players)
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing game state: ${e.message}", e)
                }
            }

            // try to parse dice‐roll JSON first
            try {
                val obj = gson.fromJson(text, DiceRollMessage::class.java)
                if (obj.type == "DICE_ROLL") {
                    onDiceRolled(obj.playerId, obj.value)
                    return  // don't fall through to the generic log handler
                }
            } catch (_: Exception) { /* not a dice‐roll */ }

            // Always call the general message handler
            onMessageReceived(text)
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

    /**
     * Sends a property buy request to the server.
     * @param propertyId The ID of the property to buy
     */
    fun buyProperty(propertyId: Int) {
        val message = "BUY_PROPERTY:$propertyId"
        webSocket?.send(message)
        Log.d("WebSocket", "Sent: $message")
    }

    private var propertyBoughtListener: ((String) -> Unit)? = null

    fun setPropertyBoughtListener(listener: (String) -> Unit) {
        propertyBoughtListener = listener
    }


    private fun loadServerUrl(context: Context): String {
        val properties = Properties()
        context.assets.open("config.properties").use { input ->
            properties.load(input)
        }
        return properties.getProperty("server.url")
    }

}