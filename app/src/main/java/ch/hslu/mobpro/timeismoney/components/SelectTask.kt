package ch.hslu.mobpro.timeismoney.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.hslu.mobpro.timeismoney.room.Task

@Composable
fun SelectTask(
    items: List<Task>? = null,
    selected: Task? = null,
    onItemSelected: (Task) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .clickable { expanded = true }
    ) {
        Text(
            text = selected?.title ?: "Aufgabe auswählen",
            modifier = Modifier.padding(16.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items?.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = item.title,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                )
            }
        }
    }
}