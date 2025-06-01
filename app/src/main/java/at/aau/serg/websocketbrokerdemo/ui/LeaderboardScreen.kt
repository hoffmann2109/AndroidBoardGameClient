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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.FirestoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(onBack: () -> Unit, currentUsername: String?) {
    val leaderboardTypes = listOf("gamesPlayed", "highestMoney", "level", "averageMoney", "wins")
    var selectedLeaderboard by remember { mutableStateOf(leaderboardTypes[0]) }
    var entries by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLeaderboard) {
        isLoading = true
        scope.launch {
            entries = FirestoreManager.getLeaderboardEntries(selectedLeaderboard)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboards") },
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
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Leaderboard Type Selection
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedLeaderboard.replaceFirstChar { it.titlecase() },
                    onValueChange = {},
                    label = { Text("Select Leaderboard") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = PrimaryNotEditable , enabled = true)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    leaderboardTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.replaceFirstChar { it.titlecase() }) },
                            onClick = {
                                selectedLeaderboard = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leaderboard Entries
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(entries) { entry ->
                        LeaderboardEntryItem(
                            entry = entry,
                            selectedLeaderboard = selectedLeaderboard,
                            currentUsername = currentUsername
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEntryItem(
    entry: Map<String, Any>,
    selectedLeaderboard: String,
    currentUsername: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry["name"] == currentUsername) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#${entry["rank"]}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (entry["name"] == currentUsername) FontWeight.Bold else FontWeight.Normal
                )
            )
            Text(
                text = entry["name"].toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (entry["name"] == currentUsername) FontWeight.Bold else FontWeight.Normal
                )
            )
            Text(
                text = "${entry[selectedLeaderboard]}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (entry["name"] == currentUsername) FontWeight.Bold else FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}