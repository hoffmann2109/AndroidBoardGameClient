package at.aau.serg.websocketbrokerdemo.data.messages

data class ClearChatMessage(
    override val type: String = "CLEAR_CHAT",
    val reason: String = "Game has ended"
) : GameMessage
