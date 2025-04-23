package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Filter Dropdown
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                var expanded by remember { mutableStateOf(false) }
                val filters = mapOf(
                    "all" to "All Time",
                    "week" to "Last Week",
                    "month" to "Last Month"
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        readOnly = true,
                        value = filters[selectedFilter] ?: "",
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filters.forEach { (key, value) ->
                            DropdownMenuItem(
                                text = { Text(value) },
                                onClick = {
                                    selectedFilter = key
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Statistics Cards
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(gameHistory.filter {
                    val timestampMillis = it.timestamp.toDate().time // Konvertiere zu Date Format
                    when(selectedFilter) {
                        "week" -> System.currentTimeMillis() - timestampMillis < 604_800_000
                        "month" -> System.currentTimeMillis() - timestampMillis < 2_592_000_000
                        else -> true
                    }
                }) { game ->
                    GameStatCard(game)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GameStatCard(game: GameData) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

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
                text = "Datum: ${dateFormat.format(game.timestamp.toDate())}",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Result: ${if (game.won) "WON" else "LOST"}", fontWeight = FontWeight.Bold)
                Text("Money: ₩${game.endMoney}")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Duration: ${game.durationMinutes}min")
                Text("Level +${game.levelGained}")
            }
        }
    }
}
