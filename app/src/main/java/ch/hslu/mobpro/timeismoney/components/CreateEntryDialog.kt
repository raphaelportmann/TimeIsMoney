package ch.hslu.mobpro.timeismoney.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*

@Composable
fun CreateEntryDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var entryText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Neue Aufgabe") },
        text = {
            TextField(
                value = entryText,
                onValueChange = { entryText = it },
                placeholder = { Text(text = "Aufgabe eingeben") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(entryText)
                    onDismiss()
                }
            ) {
                Text(text = "Erstellen")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = "Abbrechen")
            }
        }
    )
}