package ch.hslu.mobpro.timeismoney.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.hslu.mobpro.timeismoney.room.Task

@Composable
fun SelectTask(
    items: List<Task>? = null,
    selected: Task? = null,
    enabled: Boolean = true,
    onItemSelected: (Task) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .width(250.dp)
            .clickable { expanded = enabled }
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Text(
                text = selected?.title ?: "Aufgabe auswählen",
                modifier = Modifier.padding(16.dp)
            )
            Icon(Icons.Filled.ArrowDropDown, "DropDown")
        }

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