package at.aau.serg.websocketbrokerdemo.data.messages

data class GiveUpMessage (
    override val type:String="GIVE_UP",
    val userId:String,
): GameMessage
