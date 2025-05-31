package at.aau.serg.websocketbrokerdemo.data.messages

data class DealResponseMessage(
    val type: String = "DEAL_RESPONSE",
    val fromPlayerId: String,
    val toPlayerId: String,
    val responseType: DealResponseType,
    val counterPropertyIds: List<Int>,
    val counterMoney: Int
)