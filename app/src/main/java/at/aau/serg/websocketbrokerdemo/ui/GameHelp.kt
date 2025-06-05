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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.text.style.TextAlign

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
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
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
}

@Composable
fun RuleCard(
    title: String,
    description: String,
    emoji: String,
    backgroundColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$emoji  $title",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}


@Composable
fun RulesContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "🎲 Monopoly Spielregeln",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RuleCard(
            title = "Startkapital",
            description = "Jeder Spieler startet mit 1500 EUR. Die Bank verwaltet das restliche Geld, Grundstücke und Gebäude.",
            emoji = "💰",
            backgroundColor = Color(0xFFD0F0C0) // mintgrün
        )
        RuleCard(
            title = "Ziel des Spiels",
            description = "Bringe alle anderen Spieler in den Bankrott. Wer als letzter übrig bleibt, gewinnt.",
            emoji = "🎯",
            backgroundColor = Color(0xFFFFF3B0) // sanftes gelb
        )
        RuleCard(
            title = "Würfeln & Bewegen",
            description = "Würfle und ziehe deine Spielfigur entsprechend der Augenzahl.",
            emoji = "🎲",
            backgroundColor = Color(0xFFE0F7FA) // hellblau
        )
        RuleCard(
            title = "Grundstücke kaufen",
            description = "Freie Grundstücke kannst du kaufen, bei fremden musst du Miete zahlen.",
            emoji = "🏠",
            backgroundColor = Color(0xFFFFE0E0) // hellrot/pink
        )
        RuleCard(
            title = "Bauen & Vermieten",
            description = "Baue Häuser oder Hotels und verlange höhere Mieten.",
            emoji = "🏗️",
            backgroundColor = Color(0xFFEDE7F6) // lila-ton
        )
        RuleCard(
            title = "Sonderkarten",
            description = "Chance- und Gemeinschaftskarten lösen Spezialeffekte aus.",
            emoji = "🎁",
            backgroundColor = Color(0xFFFFF9C4) // zartes gelb
        )
        RuleCard(
            title = "Bankrott",
            description = "Wer kein Geld mehr hat, verliert das Spiel.",
            emoji = "❌",
            backgroundColor = Color(0xFFFFCDD2) // rosa
        )
    }
}

@Composable
fun LobbyHelp() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🏠 Lobby-Hilfe", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        listOf(
            "📡 Klicke auf Connect, um dich mit dem Server zu verbinden.",
            "🎮 Danach auf Join Game, um einem Spiel beizutreten.",
            "💬 Du kannst über den Chat Nachrichten schreiben.",
            "🚀 Wenn genug Spieler verbunden sind, startet das Spiel automatisch."
        ).forEach {
            Text(
                it,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BoardHelp() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🗺️ Spielfeld-Erklärung", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        listOf(
            "🏁 Unten rechts ist das START-Feld – 200 EUR beim Überqueren.",
            "🃏 Links oben: Gemeinschaftsfeld mit Effekten.",
            "🚓 Oben rechts: Gefängnis – bleibst 3 Runden oder zahlst.",
            "🎲 Roll Dice bewegt dich.",
            "🧍‍♂️ Rechte Seite: deine Position, Geld, Farbe.",
            "🖥️ Unten: Chat & Terminal öffnen.",
            "🙈 Give Up = Spiel aufgeben.",
            "🏠 Back to Lobby bringt dich zurück."
        ).forEach {
            Text(
                it,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
