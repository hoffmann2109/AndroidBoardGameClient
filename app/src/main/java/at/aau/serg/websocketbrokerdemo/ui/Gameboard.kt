package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import at.aau.serg.websocketbrokerdemo.data.properties.Property
import coil.compose.rememberAsyncImagePainter

@Composable
fun Gameboard(
    modifier: Modifier = Modifier,
    outerTileColor: Color = Color.DarkGray,
    innerTileColor: Color = Color.LightGray,
    onTileClick: (tilePosition: Int) -> Unit = {},
    players: List<PlayerMoney> = emptyList(),
    properties: List<Property>
) {
    val playerColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow)

    Box(
        modifier = modifier
            .background(Color.Gray)
            .testTag("gameboard")
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
                        val tilePosition = calculateTilePosition(row, col)
                        val playersOnTile = players.filter { it.position == tilePosition }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(enabled = tilePosition >= 0) {
                                    onTileClick(tilePosition)
                                }
                                .semantics { contentDescription = "Tile($row,$col)" }
                                .testTag("tile_${row}_$col"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (tilePosition == 0) {
                                val painter = rememberAsyncImagePainter("file:///android_asset/start_0.png")
                                Image(
                                    painter = painter,
                                    contentDescription = "Start Field",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(if (isOuter) outerTileColor else innerTileColor)
                                )
                            }

                            if (isOuter && tilePosition >= 0 && playersOnTile.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxSize(0.8f)
                                        .testTag("players_row_${row}_$col"),
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
                                                .semantics { contentDescription = "Player(${player.id})" }
                                                .testTag("playerCircle_${player.id}")
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

fun calculateTilePosition(row: Int, col: Int): Int {
    return when {
        row == 10 && col == 10 -> 0
        row == 10 && col == 0 -> 10
        row == 0 && col == 0 -> 20
        row == 0 && col == 10 -> 30
        row == 10 -> if (col in 1..9) 10 - col else -1
        col == 0 -> if (row in 1..9) 10 + (10 - row) else -1
        row == 0 -> if (col in 1..9) 20 + col else -1
        col == 10 -> if (row in 1..9) 30 + row else -1
        else -> -1
    }
}
