package at.aau.serg.websocketbrokerdemo.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    outerTileColor: Color = Color(0xFFE7FFC9),
    innerTileColor: Color = Color.LightGray,
    onTileClick: (tilePosition: Int) -> Unit = {},
    players: List<PlayerMoney> = emptyList(),
    properties: List<Property>
) {
    // TODO: Add images of the game pieces instead of the circles
    // Simple colors for all the 4 players
    val playerColors = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow
    )

    val centerPainter = rememberAsyncImagePainter("file:///android_asset/Center2.png")

    Box(
        modifier = modifier
            .padding(8.dp)
            .background(outerTileColor)
            .testTag("gameboard")
    ) {
        // Center image inside the board
        Image(
            painter = centerPainter,
            contentDescription = "Monopoly Center",
            modifier = Modifier
                .fillMaxSize(0.88f)
                .align(Alignment.Center)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                ),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TODO: Add a list of properties instead of the blank tiles later
            repeat(11) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    repeat(11) { col ->
                        val isOuter = row == 0 || row == 10 || col == 0 || col == 10

                        // Calculate position 0 - 39 clockwise:
                        val tilePosition = calculateTilePosition(row, col)

                        // Search for all players on a tile:
                        val playersOnTile = players.filter { it.position == tilePosition }

                        val property = properties.find { it.position == tilePosition }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(if (isOuter) outerTileColor else Color.Transparent)
                                .clickable(enabled = tilePosition in 0..39) {
                                    onTileClick(tilePosition)
                                }
                                .semantics { contentDescription = "Tile($row,$col)" }
                                .testTag("tile_${row}_$col"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (tilePosition in 0..39) {
                                val imageName = "Feld ${tilePosition + 1}.png"
                                val tilePainter =
                                    rememberAsyncImagePainter("file:///android_asset/$imageName")
                                Image(
                                    painter = tilePainter,
                                    contentDescription = "Field $tilePosition",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        if (isOuter && playersOnTile.isNotEmpty()) {
                            playersOnTile.forEachIndexed { index, player ->
                                val playerIndex = players.indexOfFirst { it.id == player.id }
                                val colorIndex =
                                    if (playerIndex >= 0) playerIndex % playerColors.size else index % playerColors.size

                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .offset(x = (-2).dp, y = (-2).dp) // leichte Verschiebung zur Mitte
                                        .background(playerColors[colorIndex], CircleShape)
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


/**
 * 0 = Start-Tile
 * Indices are increased clockwise (0-39)
 */
fun calculateTilePosition(row: Int, col: Int): Int {
    return when {
        // Corners:
        row == 10 && col == 10 -> 0  // Bottom-right corner (Start)
        row == 10 && col == 0 -> 10  // Bottom-left corner
        row == 0 && col == 0 -> 20   // Top-left corner
        row == 0 && col == 10 -> 30  // Top-right corner

        // Normal Tiles:
        row == 10 -> if (col > 0 && col < 10) 10 - col else -1  // Bottom row (1-9)
        col == 0 -> if (row > 0 && row < 10) 10 + (10 - row) else -1  // Left column (11-19)
        row == 0 -> if (col > 0 && col < 10) 20 + col else -1  // Top row (21-29)
        col == 10 -> if (row > 0 && row < 10) 30 + row else -1  // Right column (31-39)

        else -> -1  // Inner or invalid positions
    }
}