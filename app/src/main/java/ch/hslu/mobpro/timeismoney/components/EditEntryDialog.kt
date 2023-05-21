package ch.hslu.mobpro.timeismoney.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ch.hslu.mobpro.timeismoney.MainViewModel
import ch.hslu.mobpro.timeismoney.room.Entry
import ch.hslu.mobpro.timeismoney.room.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun EditEntryDialog(
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
    var selectedDate by remember { mutableStateOf(selectedDate) }
    var selectedStartTime by remember { mutableStateOf(selectedStartTime) }
    var selectedEndTime by remember { mutableStateOf(selectedEndTime) }
    val allTasks by viewModel.allTasks.observeAsState()
    var selectedTask by remember {
        mutableStateOf(allTasks?.filter { task: Task ->
            task.id == selectedTask?.id ?: pref.getLong("lastTask", -1)
        }?.firstOrNull())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Eintrag bearbeiten") },
        text = {
            Column() {
                Row() {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    },
                                    selectedDate.year,
                                    selectedDate.monthValue - 1,
                                    selectedDate.dayOfMonth
                                ).show()
                            },
                    )
                }

                Row() {
                    Text(
                        text = selectedStartTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        selectedStartTime = LocalTime.of(hour, minute, 0)
                                    },
                                    selectedStartTime.hour,
                                    selectedStartTime.minute,
                                    true
                                ).show()
                            },
                    )
                }

                Row() {
                    Text(
                        text = selectedEndTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        selectedEndTime = LocalTime.of(hour, minute, 0)
                                    },
                                    selectedEndTime.hour,
                                    selectedEndTime.minute,
                                    true
                                ).show()
                            },
                    )
                }
                SelectTask(
                    items = allTasks,
                    selected = selectedTask,
                    enabled = true
                ) {
                    selectedTask = it
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    if (selectedTask != null) {
                        onConfirm(
                            selectedDate,
                            selectedStartTime,
                            selectedEndTime,
                            selectedTask?.id ?: -1
                        )
                        onDismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Bitte wähle einen Task aus!",
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