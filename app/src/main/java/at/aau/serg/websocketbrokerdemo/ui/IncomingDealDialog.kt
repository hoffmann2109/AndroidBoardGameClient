package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.aau.serg.websocketbrokerdemo.data.messages.DealProposalMessage

@Composable
fun IncomingDealDialog(
    proposal: DealProposalMessage,
    senderName: String,
    receiverProperties: List<Int>,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onCounter: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        title = { Text("Incoming Deal") },
        text = {
            Column {
                Text("$senderName offers you a deal:")
                Text("They offer: ${proposal.offeredPropertyIds} + ${proposal.offeredMoney}€")
                Text("They want: ${proposal.requestedPropertyIds}")
            }
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Accept")
            }
        },
        dismissButton = {
            Row {
                Button(onClick = onDecline) {
                    Text("Decline")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onCounter) {
                    Text("Counter")
                }
            }
        }
    )
}
