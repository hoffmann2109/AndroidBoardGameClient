package at.aau.serg.websocketbrokerdemo.data

data class ChatMessage(
    val type:String="CHAT_MESSAGE",
    val playerId:String,
    val message:String
)
