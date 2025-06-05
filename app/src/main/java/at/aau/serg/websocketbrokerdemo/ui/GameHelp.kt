package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameHelp(onClose: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Spielregeln", "Lobby", "Spielfeld")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)) // halbtransparent
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Text(
                            text = title,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .clickable { selectedTab = index },
                            color = if (selectedTab == index) Color.Red else Color.DarkGray,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(end = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> RulesContent()
                    1 -> LobbyHelp()
                    2 -> BoardHelp()
                }
            }
        }
    }
}

@Composable
fun RulesContent() {
    Column {
        Text("🎲 Monopoly Spielregeln", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("• Ziel: Bringe alle anderen Spieler in den Bankrott.")
        Text("• Jeder startet mit 1500 EUR.")
        Text("• Du bewegst dich durch Würfeln.")
        Text("• Freie Grundstücke kannst du kaufen.")
        Text("• Bei fremden Grundstücken musst du Miete zahlen.")
        Text("• Du kannst Häuser und Hotels bauen, um Miete zu erhöhen.")
        Text("• Chance- und Gemeinschaftskarten haben spezielle Effekte.")
        Text("• Wer kein Geld mehr hat, verliert.")
    }
}

@Composable
fun LobbyHelp() {
    Column {
        Text("🏠 Lobby-Hilfe", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("1. Klicke auf **Connect**, um dich mit dem Server zu verbinden.")
        Text("2. Danach auf **Join Game**, um einem Spiel beizutreten.")
        Text("3. Du kannst optional über den Chat Nachrichten schreiben.")
        Text("4. Wenn genug Spieler verbunden sind, startet das Spiel automatisch.")
    }
}

@Composable
fun BoardHelp() {
    Column {
        Text("🗺️ Spielfeld-Erklärung", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("• Unten rechts ist das **START-Feld** – 200 EUR beim Überqueren.")
        Text("• Links oben: **Gemeinschaftsfeld** mit zufälligen Effekten.")
        Text("• Oben rechts: **Gefängnis** – du bleibst dort für 3 Züge oder zahlst.")
        Text("• Der Button **Roll Dice** (oben links) bewegt deine Spielfigur.")
        Text("• Rechts siehst du deine Position, dein Geld und deine Farbe.")
        Text("• Unten kannst du den **Terminal** oder **Chat** öffnen.")
        Text("• Mit **Give Up** kannst du das Spiel aufgeben.")
        Text("• **Back to Lobby** bringt dich zurück zur Lobby.")
    }
}
