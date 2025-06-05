package at.aau.serg.websocketbrokerdemo.data.messages

class ShakeMessage(
    override val type: String = "SHAKE_REQUEST",
    val playerId: String,
):GameMessage