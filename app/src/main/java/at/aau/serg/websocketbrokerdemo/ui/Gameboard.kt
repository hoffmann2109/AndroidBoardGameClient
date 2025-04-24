package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Gameboard(
    modifier: Modifier = Modifier,
    outerTileColor: Color = Color.DarkGray,
    innerTileColor: Color = Color.LightGray,
    onTileClick: (row: Int, col: Int) -> Unit = { _, _ -> }
) {
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
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(if (isOuter) outerTileColor else innerTileColor)
                                .clickable { onTileClick(row, col) }
                        )
                    }
                }
            }
        }
    }
}