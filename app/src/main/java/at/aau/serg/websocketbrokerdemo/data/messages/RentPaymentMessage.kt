package at.aau.serg.websocketbrokerdemo.data.messages

data class RentPaymentMessage(
    override val type: String = "RENT_PAYMENT",
    val playerId: String,
    val propertyId: Int
): GameMessage