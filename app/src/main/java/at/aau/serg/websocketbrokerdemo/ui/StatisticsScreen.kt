package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.FirestoreManager
import at.aau.serg.websocketbrokerdemo.data.GameData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(userId: String?, onBack: () -> Unit) {
    var gameHistory by remember { mutableStateOf<List<GameData>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("all") }
    var selectedMetric by remember { mutableStateOf(Metric.WINS) }
    var chartType by remember { mutableStateOf(ChartType.BAR) }
    var showChartView by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showChartView = !showChartView }) {
                        Icon(
                            imageVector = if (showChartView) Icons.Default.Search else Icons.Default.Menu,
                            contentDescription = "Toggle View"
                        )
                    }
                    if (showChartView) {
                        ChartTypeToggle(chartType) { chartType = it }
                        MetricDropdown(selectedMetric) { selectedMetric = it }
                    }
                    FilterDropdown(selectedFilter) { selectedFilter = it }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (chartData, maxValue) = calculateChartData(gameHistory, selectedFilter, selectedMetric)

            if (showChartView) {
                ChartContainer(
                    chartData = chartData,
                    maxValue = maxValue,
                    chartType = chartType,
                    modifier = Modifier
                        .height(250.dp)
                        .padding(16.dp)
                )
            } else {
                GameHistoryList(
                    games = gameHistory,
                    filter = selectedFilter,
                    selectedMetric = selectedMetric
                )
            }
        }
    }

    LaunchedEffect(userId) {
        userId?.let {
            coroutineScope.launch {
                gameHistory = FirestoreManager.getGameHistory(it)
            }
        }
    }
}

internal fun calculateChartData(
    games: List<GameData>,
    filter: String,
    metric: Metric
): Pair<Map<String, Float>, Float> {
    val filtered = games.filterByDate(filter)
    val processed = when (metric) {
        Metric.WINS -> filtered.cumulativeWins()
        else -> filtered.groupByTime(filter, metric)
    }
    val max = processed.values.maxOrNull() ?: 1f
    return Pair(processed, max)
}

private enum class ChartType { BAR, LINE }

internal enum class Metric(val displayName: String) {
    WINS("Wins"),
    LEVEL("Level"),
    MONEY("Money"),
    DURATION("Duration")
}

@Composable
private fun ChartContainer(
    chartData: Map<String, Float>,
    maxValue: Float,
    chartType: ChartType,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (chartType) {
            ChartType.BAR -> BarChart(
                data = chartData,
                maxValue = maxValue
            )
            ChartType.LINE -> LineChart(
                data = chartData,
                maxValue = maxValue
            )
        }
    }
}

@Composable
private fun BarChart(
    data: Map<String, Float>,
    maxValue: Float
) {
    val barColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.entries.sortedBy { it.key }.forEach { (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((value / maxValue * 200).dp)
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
private fun LineChart(
    data: Map<String, Float>,
    maxValue: Float
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val points = data.entries.sortedBy { it.key }.map { it.value }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (points.isEmpty()) return@Canvas

        val spacePerPoint = size.width / (points.size - 1)
        val heightRatio = size.height / maxValue

        val path = Path().apply {
            moveTo(0f, size.height - (points.first() * heightRatio))
            points.forEachIndexed { index, value ->
                val x = spacePerPoint * index
                val y = size.height - (value * heightRatio)
                lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        points.forEachIndexed { index, value ->
            val x = spacePerPoint * index
            val y = size.height - (value * heightRatio)
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun ChartTypeToggle(currentType: ChartType, onTypeSelected: (ChartType) -> Unit) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
    ) {
        ChartType.entries.forEach { type ->
            TextButton(
                onClick = { onTypeSelected(type) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (currentType == type) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            ) {
                Icon(
                    imageVector = when (type) {
                        ChartType.BAR -> Icons.Default.Search
                        ChartType.LINE -> Icons.Default.Menu
                    },
                    contentDescription = type.name
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetricDropdown(currentMetric: Metric, onMetricSelected: (Metric) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            readOnly = true,
            value = currentMetric.displayName,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().width(100.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Metric.entries.forEach { metric ->
                DropdownMenuItem(
                    text = { Text(metric.displayName) },
                    onClick = {
                        onMetricSelected(metric)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(currentFilter: String, onFilterSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val filters = mapOf(
        "all" to "All Time",
        "week" to "Last Week",
        "month" to "Last Month"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            readOnly = true,
            value = filters[currentFilter] ?: "",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().width(120.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filters.forEach { (key, value) ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        onFilterSelected(key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun GameHistoryList(
    games: List<GameData>,
    filter: String,
    selectedMetric: Metric
) {
    val filteredGames = remember(games, filter) {
        games.filterByDate(filter)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(filteredGames) { game ->
            GameStatCard(game, selectedMetric)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GameStatCard(game: GameData, selectedMetric: Metric) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val highlightColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (game.won) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dateFormat.format(game.timestamp.toDate()),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            StatRow("Result", if (game.won) "WON" else "LOST", Metric.WINS == selectedMetric, highlightColor)
            StatRow("Money", "₩${game.endMoney}", Metric.MONEY == selectedMetric, highlightColor)
            StatRow("Level", "+${game.levelGained}", Metric.LEVEL == selectedMetric, highlightColor)
            StatRow("Duration", "${game.durationMinutes}min", Metric.DURATION == selectedMetric, highlightColor)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, highlight: Boolean, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (highlight) color else Color.Transparent)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

internal fun List<GameData>.filterByDate(filter: String): List<GameData> {
    return this.filter {
        val timestamp = it.timestamp.toDate().time
        when (filter) {
            "week" -> System.currentTimeMillis() - timestamp < 604_800_000
            "month" -> System.currentTimeMillis() - timestamp < 2_592_000_000
            else -> true
        }
    }.sortedBy { it.timestamp.toDate().time }
}

internal fun List<GameData>.cumulativeWins(): Map<String, Float> {
    var cumulative = 0
    return this.associate { game ->
        cumulative += if (game.won) 1 else 0
        Pair(game.getTimeLabel(), cumulative.toFloat())
    }
}

internal fun List<GameData>.groupByTime(filter: String, metric: Metric): Map<String, Float> {
    val format = when (filter) {
        "week" -> SimpleDateFormat("EEE", Locale.getDefault())
        "month" -> SimpleDateFormat("dd", Locale.getDefault())
        else -> SimpleDateFormat("MMM", Locale.getDefault())
    }

    return groupBy {
        format.format(it.timestamp.toDate())
    }.mapValues { entry ->
        when (metric) {
            Metric.MONEY -> entry.value.sumOf { it.endMoney }.toFloat()
            Metric.LEVEL -> entry.value.sumOf { it.levelGained }.toFloat()
            Metric.DURATION -> entry.value.sumOf { it.durationMinutes }.toFloat()
            else -> 0f
        }
    }
}

private fun GameData.getTimeLabel(): String {
    val formatter = SimpleDateFormat("dd.MM", Locale.getDefault())
    return formatter.format(timestamp.toDate())
}