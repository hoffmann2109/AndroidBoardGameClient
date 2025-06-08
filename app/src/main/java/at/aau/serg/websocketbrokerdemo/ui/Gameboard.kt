package at.aau.serg.websocketbrokerdemo.ui

import com.example.myapplication.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun Gameboard(
    modifier: Modifier = Modifier,
    onTileClick: (tilePosition: Int) -> Unit = {},
    players: List<PlayerMoney> = emptyList(),
    cheatFlags: Map<String, Boolean>
) {
    val context = LocalContext.current
    val showTestToast = remember { mutableStateOf(true) }
    // Make the corners bigger like in a real monopoly board
    val cornerFactor = 1.5f
    val regularFactor = 1f
    val tokenSize: Dp = 100.dp

    val pullIn = 12.dp
    val offsets = listOf(
        pullIn to pullIn,     // TopStart → right/down
        -pullIn to pullIn,    // TopEnd   → left /down
        pullIn to -pullIn,    // BottomStart → right/up
        -pullIn to -pullIn    // BottomEnd → left/up
    )

    //Changed the circles to pictures
    val playerImages = listOf(
        R.drawable.player_red,
        R.drawable.player_blue,
        R.drawable.player_green,
        R.drawable.player_yellow
    )
    val cheatImages = listOf(
        R.drawable.player_red_cheat,
        R.drawable.player_blue_cheat,
        R.drawable.player_green_cheat,
        R.drawable.player_yellow_cheat
    )

    val boardPainter = painterResource(R.drawable.monopoly_board)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(0.dp))
            .testTag("gameboard")
    ) {
        Image(
            painter = boardPainter,
            contentDescription = "Monopoly board",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(Modifier.fillMaxSize()) {
            repeat(11) { row ->
                val rowWeight = if (row == 0 || row == 10) cornerFactor else regularFactor

                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(rowWeight),
                    verticalAlignment = Alignment.Bottom
                ) {
                    repeat(11) { col ->
                        val tilePos = calculateTilePosition(row, col)
                        val playersOnTile = players.filter { it.position == tilePos }
                        val isCorner = (row == 0 || row == 10) && (col == 0 || col == 10)
                        val tileWeight = if (isCorner) cornerFactor else regularFactor

                        Box(
                            modifier = Modifier
                                .weight(tileWeight)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .background(Color.Transparent)
                                .clickable(enabled = tilePos >= 0) { onTileClick(tilePos) }
                                .semantics { contentDescription = "Tile($row,$col)" }
                                .testTag("tile_${row}_$col"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (playersOnTile.isNotEmpty()) {
                                val gridAlignments = listOf(
                                    Alignment.TopStart,
                                    Alignment.TopEnd,
                                    Alignment.BottomStart,
                                    Alignment.BottomEnd
                                )

                                Box(modifier = Modifier.fillMaxSize(0.8f)) {
                                    playersOnTile.forEachIndexed { idxOnTile, player ->
                                        // find which slot this player is in (0–3)
                                        val slotIndex = players.indexOfFirst { it.id == player.id }
                                        val cheated = cheatFlags[player.id] == true

                                        val imageRes = if (cheated)
                                            cheatImages.getOrElse(slotIndex) { playerImages[slotIndex] }
                                        else
                                            playerImages.getOrElse(slotIndex) { playerImages[0] }

                                        // Apply offsets only if multiple game pieces are on a tile
                                        val applyOffset = if (playersOnTile.size > 1)
                                            offsets.getOrElse(idxOnTile) { 0.dp to 0.dp }
                                        else
                                            0.dp to 0.dp

                                        Image(
                                            painter = painterResource(id = imageRes),
                                            contentDescription = "Player ${player.id}" + if (cheated) " (cheater)" else "",
                                            modifier = Modifier
                                                .size(tokenSize)
                                                .align(gridAlignments[idxOnTile])
                                                .offset(
                                                    x = applyOffset.first,
                                                    y = applyOffset.second
                                                )
                                                .clip(CircleShape)
                                                .testTag("playerImage_${player.id}"),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Show toast on first render (test Pasch)
        LaunchedEffect(showTestToast.value) {
            if (showTestToast.value) {
                Toast.makeText(context, "🎉 Pasch gewürfelt!", Toast.LENGTH_SHORT).show()
                showTestToast.value = false
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
        row == 10 && col == 10 -> 0   // Bottom-right corner (Start)
        row == 10 && col == 0 -> 10  // Bottom-left corner
        row == 0 && col == 0 -> 20  // Top-left corner
        row == 0 && col == 10 -> 30  // Top-right corner

        row == 10 -> if (col in 1..9) 10 - col else -1
        col == 0 -> if (row in 1..9) 10 + (10 - row) else -1
        row == 0 -> if (col in 1..9) 20 + col else -1
        col == 10 -> if (row in 1..9) 30 + row else -1

        else -> -1
    }
}
