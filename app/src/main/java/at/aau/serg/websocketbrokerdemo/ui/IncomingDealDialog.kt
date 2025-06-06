package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.messages.DealProposalMessage
import at.aau.serg.websocketbrokerdemo.data.properties.Property
import at.aau.serg.websocketbrokerdemo.data.properties.getDrawableIdFromName

@Composable
fun IncomingDealDialog(
    proposal: DealProposalMessage,
    senderName: String,
    receiverProperties: List<Int>,
    allProperties: List<Property>,
    isMyTurn: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onCounter: () -> Unit
) {
    val context = LocalContext.current

    val offeredProperties = allProperties.filter { proposal.offeredPropertyIds.contains(it.id) }
    val requestedProperties = allProperties.filter { proposal.requestedPropertyIds.contains(it.id) }

    AlertDialog(
        onDismissRequest = onDecline,
        title = {
            Text("Incoming Deal")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("$senderName offers you a deal:")

                Spacer(Modifier.height(12.dp))
                Text("They offer:")
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    offeredProperties.forEach { property ->
                        val imageId = getDrawableIdFromName(property.image, context)
                        if (imageId != 0) {
                            Image(
                                painter = painterResource(imageId),
                                contentDescription = property.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(4.dp)
                            )
                        } else {
                            Text(property.name, modifier = Modifier.padding(4.dp))
                        }
                    }
                    if (proposal.offeredMoney > 0) {
                        Text("+ ${proposal.offeredMoney} €", modifier = Modifier.padding(8.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("They want from you:")
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    requestedProperties.forEach { property ->
                        val imageId = getDrawableIdFromName(property.image, context)
                        if (imageId != 0) {
                            Image(
                                painter = painterResource(imageId),
                                contentDescription = property.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(4.dp)
                            )
                        } else {
                            Text(property.name, modifier = Modifier.padding(4.dp))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("You currently own: ${receiverProperties.size} properties")
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                enabled = isMyTurn,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMyTurn) Color(0xFF4CAF50) else Color.LightGray
                )
            ) {
                Text("Accept", color = if (isMyTurn) Color.White else Color.DarkGray)
            }
        },
        dismissButton = {
            Row {
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Decline", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onCounter,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMyTurn) Color(0xFFFF9800) else Color.LightGray)
                ) {
                    Text("Counter", color = if (isMyTurn) Color.White else Color.DarkGray)
                }
            }
        }
    )
}


