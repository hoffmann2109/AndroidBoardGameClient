package at.aau.serg.websocketbrokerdemo

import com.google.gson.Gson
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import at.aau.serg.websocketbrokerdemo.data.messages.DealProposalMessage
import at.aau.serg.websocketbrokerdemo.data.messages.DealResponseMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageParserTest {

    private lateinit var gson: Gson
    // Note: PlayerMoney now requires a 'position' field
    private val dummyPlayers = listOf(
        PlayerMoney(id = "p1", name = "Alice", money = 0, position = 0),
        PlayerMoney(id = "p2", name = "Bob",   money = 0, position = 1)
    )

    @BeforeEach
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun testTaxPayment() {
        val json = """{"type":"TAX_PAYMENT","playerId":"p1","amount":250,"taxType":"INCOME"}"""
        var invoked = false
        var capName: String? = null
        var capAmt: Int? = null
        var capType: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { name, amt, type ->
                invoked = true; capName = name; capAmt = amt; capType = type
            },
            onPlayerPassedGo = { fail("should not hit passed GO") },
            onPropertyBought = { fail("should not hit property bought") },
            onGameStateReceived = { fail("should not hit game state") },
            onPlayerTurn = { fail("should not hit player turn") },
            onDiceRolled = { _, _, _, _ -> fail("should not hit dice roll") },
            onCardDrawn = { _, _, _, _ -> fail("should not hit card drawn") },
            onChatMessageReceived = { _, _ -> fail("should not hit chat") },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail("should not hit fallback") },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked, "onTaxPayment must be invoked")
        assertEquals("Alice", capName)
        assertEquals(250, capAmt)
        assertEquals("INCOME", capType)
    }

    @Test
    fun testPassedGo() {
        val raw = "Player p2 passed GO and collected 200"
        var invoked = false
        var capName: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { name -> invoked = true;capName = name },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(raw)

        assertTrue(invoked, "onPlayerPassedGo must be invoked")
        assertEquals("Bob", capName)
    }

    @Test
    fun testPropertyBought() {
        val raw = "XXX PROPERTY_BOUGHT YYY"
        var invoked = false
        var capRaw: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { rawText -> invoked = true; capRaw = rawText },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(raw)

        assertTrue(invoked)
        assertEquals(raw, capRaw)
    }

    @Test
    fun testGameState() {
        val jsonList = "[]"
        val raw = "GAME_STATE:$jsonList"
        var invoked = false
        var capList: List<PlayerMoney>? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { list -> invoked = true; capList = list },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(raw)

        assertTrue(invoked)
        assertNotNull(capList)
        assertEquals(0, capList!!.size)
    }

    @Test
    fun testPlayerTurn() {
        val raw = "PLAYER_TURN:session42"
        var invoked = false
        var capId: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { id -> invoked = true; capId = id },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(raw)

        assertTrue(invoked)
        assertEquals("session42", capId)
    }

    @Test
    fun testDiceRoll() {
        val json = """{"type":"DICE_ROLL","playerId":"p1","value":6,"manual":false,"isPasch":false}"""
        var invoked = false
        var capPid: String? = null
        var capVal: Int? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { pid, v, _, _ -> invoked = true; capPid = pid; capVal = v },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked)
        assertEquals("p1", capPid)
        assertEquals(6, capVal)
    }

    @Test
    fun testCardDrawn() {
        val json = """
        {
          "type":"CARD_DRAWN",
          "playerId":"p2",
          "cardType":"CHANCE",
          "card":{"id":2,"description":"You win!"}
        }
        """.trimIndent()
        var invoked = false
        var capPid: String? = null
        var capType: String? = null
        var capDesc: String? = null
        var capId: Int? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { pid, type, desc, id ->
                invoked = true; capPid = pid; capType = type; capDesc = desc; capId = id
            },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked)
        assertEquals("p2", capPid)
        assertEquals("CHANCE", capType)
        assertEquals("You win!", capDesc)
        assertEquals(2, capId)
    }

    @Test
    fun testChatMessage() {
        val json = """{"type":"CHAT_MESSAGE","playerId":"p1","message":"Hey!"}"""
        var invoked = false
        var capPid: String? = null
        var capMsg: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { pid, msg ->
                invoked = true
                capPid = pid
                capMsg = msg
            },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked)
        assertEquals("p1", capPid)
        assertEquals("Hey!", capMsg)
    }

    @Test
    fun testCheatMessage() {
        val json = """{"type":"CHEAT_MESSAGE","playerId":"p2","message":"kill all"}"""
        var invoked = false
        var capPid: String? = null
        var capMsg: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { pid, msg ->
                invoked = true
                capPid = pid
                capMsg = msg
            },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail("should not hit fallback") },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked, "onCheatMessageReceived must be invoked")
        assertEquals("p2", capPid)
        assertEquals("kill all", capMsg)
    }

    @Test
    fun testClearChat() {
        val json = """{"type":"CLEAR_CHAT","reason":"Reset"}"""
        var invoked = false

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail() },
            onClearChat = { invoked = true },
            onHasWon = { _ -> },
            onMessageReceived = { fail() },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)
        assertTrue(invoked)
    }

    @Test
    fun testHasWonMessage() {
        val json = """{"type":"HAS_WON","userId":"p2"}"""
        var invoked = false
        var capWinner: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail() },
            onClearChat = { },
            onHasWon = { winnerId ->
                invoked = true
                capWinner = winnerId
            },
            onMessageReceived = { fail("should not hit fallback") },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked, "onHasWon must be invoked")
        assertEquals("p2", capWinner)
    }

    @Test
    fun testFallback() {
        val raw = "SOME_UNRECOGNIZED_MESSAGE"
        var invoked = false
        var capText: String? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail("should not hit cheat") },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { text ->
                invoked = true
                capText = text
            },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(raw)

        assertTrue(invoked)
        assertEquals(raw, capText)
    }

    @Test
    fun testDealProposal() {
        // Minimal JSON containing only the "type" field → parser should invoke onDealProposal
        val json = """{"type":"DEAL_PROPOSAL"}"""
        var invoked = false
        var captured: DealProposalMessage? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail() },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail("should not hit fallback") },
            onDealProposal = { proposal ->
                invoked = true
                captured = proposal
            },
            onDealResponse = { _: DealResponseMessage -> }
        )

        parser.parse(json)

        assertTrue(invoked, "onDealProposal must be invoked when type is DEAL_PROPOSAL")
        assertNotNull(captured)
        assertEquals("DEAL_PROPOSAL", captured?.type, "DealProposalMessage.type should be parsed as DEAL_PROPOSAL")
    }

    @Test
    fun testDealResponse() {
        // Minimal JSON containing only the "type" field → parser should invoke onDealResponse
        val json = """{"type":"DEAL_RESPONSE"}"""
        var invoked = false
        var captured: DealResponseMessage? = null

        val parser = MessageParser(
            gson = gson,
            getPlayers = { dummyPlayers },
            onTaxPayment = { _, _, _ -> fail() },
            onPlayerPassedGo = { fail() },
            onPropertyBought = { fail() },
            onGameStateReceived = { fail() },
            onPlayerTurn = { fail() },
            onDiceRolled = { _, _, _, _ -> fail() },
            onCardDrawn = { _, _, _, _ -> fail() },
            onChatMessageReceived = { _, _ -> fail() },
            onCheatMessageReceived = { _, _ -> fail() },
            onClearChat = { },
            onHasWon = { _ -> },
            onMessageReceived = { fail("should not hit fallback") },
            onDealProposal = { _: DealProposalMessage -> },
            onDealResponse = { response ->
                invoked = true
                captured = response
            }
        )

        parser.parse(json)

        assertTrue(invoked, "onDealResponse must be invoked when type is DEAL_RESPONSE")
        assertNotNull(captured)
        assertEquals("DEAL_RESPONSE", captured?.type, "DealResponseMessage.type should be parsed as DEAL_RESPONSE")
    }

}
