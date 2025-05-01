package at.aau.serg.websocketbrokerdemo.data

data class ChatEntry(
    val senderId: String,
    val senderName: String,
    val message: String
)
