package at.aau.serg.websocketbrokerdemo.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    var showChart by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        userId?.let {
            coroutineScope.launch {
                gameHistory = FirestoreManager.getGameHistory(it)
            }
        }
    }

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
                    IconButton(onClick = { showChart = !showChart }) {
                        Icon(
                            if (showChart) Icons.AutoMirrored.Filled.List else Icons.Default.Search,
                            contentDescription = "Toggle View"
                        )
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
            if (showChart) {
                SimpleBarChart(
                    games = gameHistory,
                    filter = selectedFilter,
                    modifier = Modifier
                        .height(200.dp)
                        .padding(16.dp)
                )
            }

            GameHistoryList(
                games = gameHistory,
                filter = selectedFilter
            )
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
private fun SimpleBarChart(games: List<GameData>, filter: String, modifier: Modifier = Modifier) {
    val filteredGames = remember(games, filter) {
        games.filter {
            val timestamp = it.timestamp.toDate().time
            when (filter) {
                "week" -> System.currentTimeMillis() - timestamp < 604_800_000
                "month" -> System.currentTimeMillis() - timestamp < 2_592_000_000
                else -> true
            }
        }
    }

    val maxMoney = filteredGames.maxOfOrNull { it.endMoney } ?: 1
    val dateFormat = remember { SimpleDateFormat("dd.MM", Locale.getDefault()) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            filteredGames.takeLast(7).forEach { game ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(40.dp)
                ) {
                    Text(
                        text = dateFormat.format(game.timestamp.toDate()),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((game.endMoney.toFloat() / maxMoney * 150).dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameHistoryList(games: List<GameData>, filter: String) {
    val filteredGames = remember(games, filter) {
        games.filter {
            val timestamp = it.timestamp.toDate().time
            when (filter) {
                "week" -> System.currentTimeMillis() - timestamp < 604_800_000
                "month" -> System.currentTimeMillis() - timestamp < 2_592_000_000
                else -> true
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(filteredGames) { game ->
            GameStatCard(game)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GameStatCard(game: GameData) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Result:", fontWeight = FontWeight.Bold)
                Text(
                    text = if (game.won) "VICTORY" else "DEFEAT",
                    color = if (game.won) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }

            StatRow("Money Earned", "₩${game.endMoney}")
            StatRow("Duration", "${game.durationMinutes} minutes")
            StatRow("Players", game.playersCount.toString())
            StatRow("Level Progress", "+${game.levelGained}")
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
    }
}