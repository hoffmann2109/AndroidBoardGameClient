package at.aau.serg.websocketbrokerdemo.data

data class DiceRollMessage(
    val type:     String,
    val playerId: String,
    val value:    Int
)
