package at.aau.serg.websocketbrokerdemo.data.messages

data class HasWonMessage(
    override val type: String = "HAS_WON",
    val userId: String
): GameMessage
