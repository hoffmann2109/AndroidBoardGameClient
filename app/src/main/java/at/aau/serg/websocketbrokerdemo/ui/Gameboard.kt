package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney

@Composable
fun Gameboard(
    modifier: Modifier = Modifier,
    outerTileColor: Color = Color.DarkGray,
    innerTileColor: Color = Color.LightGray,
    onTileClick: (row: Int, col: Int) -> Unit = { _, _ -> },
    players: List<PlayerMoney> = emptyList()
) {
    // Farben für Spieler 1 - 4 (später werden Bilder von Figuren hinzugefügt)
    val playerColors = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow
    )

    Box(
        modifier = modifier
            .background(Color.Gray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            repeat(11) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    repeat(11) { col ->
                        val isOuter = row == 0 || row == 10 || col == 0 || col == 10

                        // Feldposition von 0 - 39 im Uhrzeigersinn berechnen:
                        val tilePosition = calculateTilePosition(row, col)

                        // Alle Spieler auf einem Feld suchen:
                        val playersOnTile = players.filter { it.position == tilePosition }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(if (isOuter) outerTileColor else innerTileColor)
                                .clickable { onTileClick(row, col) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isOuter && tilePosition >= 0 && playersOnTile.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxSize(0.8f),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    playersOnTile.forEachIndexed { index, player ->
                                        val playerIndex = players.indexOfFirst { it.id == player.id }
                                        val colorIndex = if (playerIndex >= 0) playerIndex % playerColors.size else index % playerColors.size

                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    playerColors[colorIndex],
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 0 = Los-Feld
 * Indizes werden im Uhrzeigersinn erhöht
 */
private fun calculateTilePosition(row: Int, col: Int): Int {
    return when {
        row == 10 -> if (col >= 0 && col <= 10) 10 - col else -1 // Unten: (0-10)
        col == 0 -> if (row >= 0 && row <= 10) 10 + (10 - row) else -1 // Links: (11-20)
        row == 0 -> if (col >= 0 && col <= 10) 20 + col else -1 // Oben: (21-30)
        col == 10 -> if (row >= 1 && row <= 9) 30 + row else -1 // Rechts: (31-39)
        else -> -1 // Innen
    }
}