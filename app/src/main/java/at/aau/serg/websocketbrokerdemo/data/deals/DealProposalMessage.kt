package at.aau.serg.websocketbrokerdemo.data.deals

data class DealProposalMessage(
    val type: String = "DEAL_PROPOSAL",
    val fromPlayerId: String,
    val toPlayerId: String,
    val requestedPropertyIds: List<Int>,
    val offeredPropertyIds: List<Int>,
    val offeredMoney: Int
)