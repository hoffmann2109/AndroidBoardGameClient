package at.aau.serg.websocketbrokerdemo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import at.aau.serg.websocketbrokerdemo.data.messages.ChatMessage
import at.aau.serg.websocketbrokerdemo.data.messages.DiceRollMessage
import at.aau.serg.websocketbrokerdemo.data.messages.DrawnCardMessage
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import at.aau.serg.websocketbrokerdemo.data.messages.CheatMessage
import at.aau.serg.websocketbrokerdemo.data.messages.ClearChatMessage
import at.aau.serg.websocketbrokerdemo.data.messages.HasWonMessage
import at.aau.serg.websocketbrokerdemo.data.messages.TaxPaymentMessage
import at.aau.serg.websocketbrokerdemo.data.messages.DealProposalMessage
import at.aau.serg.websocketbrokerdemo.data.messages.DealResponseMessage


class MessageParser(
    private val gson: Gson,
    private val getPlayers: () -> List<PlayerMoney>,
    private val onTaxPayment: (playerName: String, amount: Int, taxType: String) -> Unit,
    private val onPlayerPassedGo: (playerName: String) -> Unit,
    private val onPropertyBought: (raw: String) -> Unit,
    private val onGameStateReceived: (List<PlayerMoney>) -> Unit,
    private val onPlayerTurn: (sessionId: String) -> Unit,
    private val onDiceRolled: (playerId: String, value: Int, manual: Boolean, isPasch: Boolean) -> Unit,
    private val onCardDrawn: (playerId: String, cardType: String, description: String, cardId: Int) -> Unit,
    private val onChatMessageReceived: (playerId: String, message: String) -> Unit,
    private val onCheatMessageReceived: (playerId: String, message: String) -> Unit,
    private val onClearChat: () -> Unit,
    private val onHasWon: (winnerId: String) -> Unit,
    private val onMessageReceived: (text: String) -> Unit,
    private val onDealProposal: (DealProposalMessage) -> Unit,
    private val onDealResponse: (DealResponseMessage) -> Unit,
    ) {
    fun parse(text: String) {
        // 1) TAX_PAYMENT
        try {
            val taxMessage = gson.fromJson(text, TaxPaymentMessage::class.java)
            if (taxMessage.type == "TAX_PAYMENT") {
                val playerName = getPlayers().find { it.id == taxMessage.playerId }?.name ?: "Unknown Player"
                onTaxPayment(playerName, taxMessage.amount, taxMessage.taxType)
                return
            }
        } catch (e: Exception) {
            println("Error parsing TAX_PAYMENT: ${e.message}")
        }

        // 2) PASSED GO
        if (text.contains("passed GO and collected")) {
            val playerId = text.substringAfter("Player ").substringBefore(" passed")
            val playerName = getPlayers().find { it.id == playerId }?.name ?: "Unknown Player"
            onPlayerPassedGo(playerName)
            return
        }

        // 3) PROPERTY_BOUGHT
        if (text.contains("PROPERTY_BOUGHT")) {
            onPropertyBought(text)
            return
        }

        // 4) PROPERTY_SOLD
        if (text.contains("sold property")) {
            onPropertyBought(text) // Reuse the property bought listener to update the UI
            return
        }

        // 5) GAME_STATE
        if (text.startsWith("GAME_STATE:")) {
            try {
                val jsonData = text.removePrefix("GAME_STATE:")
                val type = object : TypeToken<List<PlayerMoney>>() {}.type
                val players = gson.fromJson<List<PlayerMoney>>(jsonData, type)
                onGameStateReceived(players)
            } catch (e: Exception) {
                println("Error parsing GAME_STATE: ${e.message}")
            }
            return
        }

        // 6) PLAYER_TURN
        if (text.startsWith("PLAYER_TURN")) {
            try {
                val sessionId = text.substringAfter("PLAYER_TURN:")
                onPlayerTurn(sessionId)
                return
            } catch (e: Exception) {
                println("Error parsing PLAYER_TURN: ${e.message}")
            }
        }

        // 7) DICE_ROLL
        try {
            val roll = gson.fromJson(text, DiceRollMessage::class.java)
            if (roll.type == "DICE_ROLL") {
                onDiceRolled(roll.playerId, roll.value, roll.manual, roll.isPasch)
                return
            }
        } catch (e: Exception) {
            println("Error parsing DICE_ROLL: ${e.message}")
        }

        // 8) CARD_DRAWN
        try {
            val drawn = gson.fromJson(text, DrawnCardMessage::class.java)
            if (drawn.type == "CARD_DRAWN") {
                val desc = drawn.card["description"].asString
                val cardId = drawn.card["id"].asInt
                onCardDrawn(drawn.playerId, drawn.cardType, desc, cardId)
                return
            }
        } catch (e: Exception) {
            println("Error parsing CARD_DRAWN: ${e.message}")
        }

        // 9) CHAT_MESSAGE
        try {
            val chat = gson.fromJson(text, ChatMessage::class.java)
            if (chat.type == "CHAT_MESSAGE") {
                onChatMessageReceived(chat.playerId, chat.message)
                return
            }
        } catch (e: Exception) {
            println("Error parsing CHAT_MESSAGE: ${e.message}")
        }

        // 10) CHEAT_MESSAGE
        try {
            val cheat = gson.fromJson(text, CheatMessage::class.java)
            if (cheat.type == "CHEAT_MESSAGE") {
                onCheatMessageReceived(cheat.playerId, cheat.message)
                return
            }
        } catch (e: Exception) {
            println("Error parsing CHEAT_MESSAGE: ${e.message}")
        }
        // 11) DEAL_PROPOSAL
        try {
            val proposal = gson.fromJson(text, DealProposalMessage::class.java)
            if (proposal.type == "DEAL_PROPOSAL") {
                onDealProposal?.invoke(proposal)
                return
            }
        } catch (e: Exception) {
            println("Error parsing DEAL_PROPOSAL: ${e.message}")
        }

        // 12) DEAL_RESPONSE
        try {
            val response = gson.fromJson(text, DealResponseMessage::class.java)
            if (response.type == "DEAL_RESPONSE") {
                onDealResponse?.invoke(response)
                return
            }
        } catch (e: Exception) {
            println("Error parsing DEAL_RESPONSE: ${e.message}")
        }


        // 13) HAS_WON
        try {
            val won = gson.fromJson(text, HasWonMessage::class.java)
            if (won.type == "HAS_WON") {
                onHasWon(won.userId)
                return
            }
        } catch (e: Exception) {
            println("Error parsing HAS_WON: ${e.message}")
        }

        // 14) CLEAR_CHAT
        try {
            val clearMessage = gson.fromJson(text, ClearChatMessage::class.java)
            if (clearMessage.type == "CLEAR_CHAT") {
                onClearChat()
                return
            }
        } catch (e: Exception) {
            println("Error parsing CLEAR_CHAT: ${e.message}")
        }

        // 15) FALLBACK
        onMessageReceived(text)
    }
}
