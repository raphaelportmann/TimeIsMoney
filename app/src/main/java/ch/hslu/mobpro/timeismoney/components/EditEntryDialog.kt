package ch.hslu.mobpro.timeismoney.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ch.hslu.mobpro.timeismoney.MainViewModel
import ch.hslu.mobpro.timeismoney.room.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun EditEntryDialog(
    title: String = "Eintrag bearbeiten",
    selectedDate: LocalDate,
    selectedStartTime: LocalTime,
    selectedEndTime: LocalTime,
    selectedTask: Task?,
    viewModel: MainViewModel,
    onConfirm: (LocalDate, LocalTime, LocalTime, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val pref: SharedPreferences =
        context.getApplicationContext().getSharedPreferences(currentUser?.uid ?: "StandardUser", 0)
    var date by remember { mutableStateOf(selectedDate) }
    var startTime by remember { mutableStateOf(selectedStartTime) }
    var endTime by remember { mutableStateOf(selectedEndTime) }
    val allTasks by viewModel.allTasks.observeAsState()
    var task by remember {
        mutableStateOf(allTasks?.filter { task: Task ->
            task.id == (selectedTask?.id ?: pref.getLong("lastTask", -1))
        }?.firstOrNull())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column() {
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Datum: ")
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        date = LocalDate.of(year, month + 1, dayOfMonth)
                                    },
                                    date.year,
                                    date.monthValue - 1,
                                    date.dayOfMonth
                                ).show()
                            },
                    )
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Startzeit: ")
                    Text(
                        text = startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        startTime = LocalTime.of(hour, minute, 0)
                                    },
                                    startTime.hour,
                                    startTime.minute,
                                    true
                                ).show()
                            },
                    )
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Endzeit: ")
                    Text(
                        text = endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        endTime = LocalTime.of(hour, minute, 0)
                                    },
                                    endTime.hour,
                                    endTime.minute,
                                    true
                                ).show()
                            },
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Task: ")
                    SelectTask(
                        items = allTasks,
                        selected = task,
                        enabled = true
                    ) {
                        task = it
                    }
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    if (task != null) {
                        onConfirm(
                            date,
                            startTime,
                            endTime,
                            task?.id ?: -1
                        )
                        onDismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Bitte wähle eine Aufgabe aus!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(text = "Speichern")
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