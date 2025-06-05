package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                val selectedTabIndex = selectedTab
                Box(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                    .height(3.dp),
                                color = Color(0xFF007AFF)
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 16.sp,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }

                    // Close button oben rechts
                    Text(
                        text = "✕",
                        fontSize = 20.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { onClose() }
                            .padding(12.dp)
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "🎲 Monopoly Spielregeln",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))
        listOf(
            "• Ziel: Bringe alle anderen Spieler in den Bankrott.",
            "• Jeder startet mit 1500 EUR.",
            "• Du bewegst dich durch Würfeln.",
            "• Freie Grundstücke kannst du kaufen.",
            "• Bei fremden Grundstücken musst du Miete zahlen.",
            "• Du kannst Häuser und Hotels bauen, um Miete zu erhöhen.",
            "• Chance- und Gemeinschaftskarten haben spezielle Effekte.",
            "• Wer kein Geld mehr hat, verliert."
        ).forEach {
            Text(it, fontSize = 18.sp, modifier = Modifier.padding(4.dp), color = Color.Black)
        }
    }
}

@Composable
fun LobbyHelp() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "🏠 Lobby-Hilfe",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))
        listOf(
            "1. Klicke auf Connect, um dich mit dem Server zu verbinden.",
            "2. Danach auf Join Game, um einem Spiel beizutreten.",
            "3. Du kannst über den Chat Nachrichten schreiben.",
            "4. Sobald genug Spieler verbunden sind, startet das Spiel automatisch."
        ).forEach {
            Text(it, fontSize = 18.sp, modifier = Modifier.padding(4.dp), color = Color.Black)
        }
    }
}

@Composable
fun BoardHelp() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🗺️ Spielfeld-Erklärung", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        listOf(
            "• Unten rechts ist das START-Feld – 200 EUR beim Überqueren.",
            "• Links oben: Gemeinschaftsfeld mit Effekten.",
            "• Oben rechts: Gefängnis – bleibst 3 Runden oder zahlst.",
            "• 🎲 Roll Dice bewegt dich.",
            "• Rechte Seite: deine Position, Geld, Farbe.",
            "• Unten: Chat & Terminal öffnen.",
            "• Give Up = Spiel aufgeben.",
            "• Back to Lobby bringt dich zurück."
        ).forEach {
            Text(it, fontSize = 18.sp, modifier = Modifier.padding(4.dp), color = Color.Black)
        }
    }
}
