package at.aau.serg.websocketbrokerdemo.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.PlayerMoney
import at.aau.serg.websocketbrokerdemo.data.messages.DealProposalMessage
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import at.aau.serg.websocketbrokerdemo.data.properties.Property
import at.aau.serg.websocketbrokerdemo.data.properties.getDrawableIdFromName


@Composable
fun DealDialog(
    players: List<PlayerMoney>,
    senderId: String,
    allProperties: List<Property>,
    receiver: PlayerMoney?,
    initialRequested: List<Int> = emptyList(),
    initialOffered: List<Int> = emptyList(),
    initialMoney: Int = 0,
    onReceiverChange: (PlayerMoney) -> Unit = {},
    onSendDeal: (DealProposalMessage) -> Unit,
    onDismiss: () -> Unit
) {
    var offeredProperties by remember { mutableStateOf(initialOffered.toMutableList()) }
    var requestedProperties by remember { mutableStateOf(initialRequested.toMutableList()) }
    var offeredMoney by remember { mutableStateOf(initialMoney.toString()) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Make a Deal") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (receiver == null) {
                    Text("Choose a player:")
                    Spacer(Modifier.height(8.dp))
                    players.forEach { player ->
                        Button(
                            onClick = { onReceiverChange(player) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(player.name)
                        }
                    }
                } else {
                    Text("Dealing with: ${receiver.name}")
                    Spacer(Modifier.height(12.dp))

                    // Eigene Grundstücke (Bilder mit Checkbox)
                    Text("Your properties:")
                    val senderProperties = allProperties.filter { it.ownerId == senderId }
                    DealPropertyRow(
                        properties = senderProperties,
                        selectedIds = offeredProperties,
                        onToggle = { id, checked ->
                            if (checked) offeredProperties.add(id) else offeredProperties.remove(id)
                        },
                        context = context
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("${receiver.name}'s properties:")
                    val receiverProperties = allProperties.filter { it.ownerId == receiver.id }
                    DealPropertyRow(
                        properties = receiverProperties,
                        selectedIds = requestedProperties,
                        onToggle = { id, checked ->
                            if (checked) requestedProperties.add(id) else requestedProperties.remove(id)
                        },
                        context = context
                    )

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = offeredMoney,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) {
                                offeredMoney = it
                            }
                        },
                        label = { Text("You offer (€)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (receiver != null) {
                Button(onClick = {
                    onSendDeal(
                        DealProposalMessage(
                            type = "DEAL_PROPOSAL",
                            fromPlayerId = senderId,
                            toPlayerId = receiver.id,
                            offeredPropertyIds = offeredProperties,
                            requestedPropertyIds = requestedProperties,
                            offeredMoney = offeredMoney.toIntOrNull() ?: 0
                        )
                    )
                }) {
                    Text("Send Deal")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DealPropertyRow(
    properties: List<Property>,
    selectedIds: List<Int>,
    onToggle: (Int, Boolean) -> Unit,
    context: Context
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        properties.forEach { property ->
            val isSelected = selectedIds.contains(property.id)
            val imageResId = getDrawableIdFromName(property.image, context)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .clickable { onToggle(property.id, !isSelected) }
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = property.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = if (isSelected) 1f else 0.4f
                        )
                    } else {
                        Text(property.name, fontSize = 10.sp)
                    }
                }
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { checked -> onToggle(property.id, checked) }
                )
            }
        }
    }
}