package at.aau.serg.websocketbrokerdemo.data.messages

data class CheatMessage(
    override val type:String="CHEAT_MESSAGE",
    val playerId:String,
    val message:String
):GameMessage