package ch.hslu.mobpro.timeismoney.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import ch.hslu.mobpro.timeismoney.room.Task

@Composable
fun CreateTaskDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var taskText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Neue Aufgabe") },
        text = {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                placeholder = { Text(text = "Aufgabe eingeben") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(taskText)
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