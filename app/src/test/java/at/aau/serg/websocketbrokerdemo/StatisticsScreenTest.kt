package at.aau.serg.websocketbrokerdemo

import at.aau.serg.websocketbrokerdemo.data.GameData
import at.aau.serg.websocketbrokerdemo.logic.Metric
import at.aau.serg.websocketbrokerdemo.logic.calculateChartData
import at.aau.serg.websocketbrokerdemo.logic.cumulativeWins
import at.aau.serg.websocketbrokerdemo.logic.filterByDate
import at.aau.serg.websocketbrokerdemo.logic.groupByTime
import com.google.firebase.Timestamp
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.text.SimpleDateFormat
import java.util.*

class StatisticsScreenTest {
    private val oneDayMillis = 24 * 60 * 60 * 1000L
    private lateinit var currentTime: Date

    @BeforeEach
    fun setup() {
        currentTime = Date()
    }

    private fun createGame(daysAgo: Int, won: Boolean = false, money: Int = 0, level: Int = 0, duration: Int = 0): GameData {
        val timestamp = Timestamp(Date(currentTime.time - daysAgo * oneDayMillis))
        return GameData(
            timestamp = timestamp,
            won = won,
            endMoney = money,
            levelGained = level,
            durationMinutes = duration
        )
    }

    @Test
    fun `filterByDate should return correct results for week filter`() {
        val games = listOf(
            createGame(3),
            createGame(8)
        )

        val result = games.filterByDate("week")

        assertEquals(1, result.size)
    }

    @Test
    fun `cumulativeWins should calculate running total`() {
        val games = listOf(
            createGame(4, won = true),
            createGame(3, won = false),
            createGame(2, won = true)
        ).sortedBy { it.timestamp.toDate().time }

        val result = games.cumulativeWins()

        assertAll(
            { assertEquals(3, result.size) },
            { assertEquals(listOf(1f, 1f, 2f), result.values.toList()) }
        )
    }

    @Test
    fun `groupByTime should aggregate metrics correctly`() {
        val games = listOf(
            createGame(3, money = 100),
            createGame(3, money = 200),
            createGame(2, money = 300)
        )

        val result = games.groupByTime("week", Metric.MONEY)
        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(300f, result[dateFormat.format(games[0].timestamp.toDate())]) },
            { assertEquals(300f, result[dateFormat.format(games[2].timestamp.toDate())]) }
        )
    }

    @Test
    fun `calculateChartData should handle empty input gracefully`() {
        val (data, max) = calculateChartData(emptyList(), "all", Metric.DURATION)

        assertAll(
            { assertTrue(data.isEmpty()) },
            { assertEquals(1f, max) }
        )
    }
}