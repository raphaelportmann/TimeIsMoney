package ch.hslu.mobpro.timeismoney

import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ch.hslu.mobpro.timeismoney.components.*
import ch.hslu.mobpro.timeismoney.room.Entry
import ch.hslu.mobpro.timeismoney.room.Task
import ch.hslu.mobpro.timeismoney.room.TaskEntry
import ch.hslu.mobpro.timeismoney.service.TimeService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(navController: NavController, viewModel: MainViewModel) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val allTasks by viewModel.allTasks.observeAsState()
    var selectedTask by remember { mutableStateOf(allTasks?.firstOrNull()) }
    val allEntries by viewModel.getEntriesByTask(selectedTask?.id ?: -1).observeAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.setUserId(currentUser?.uid ?: "")
        selectedTask = null

    }

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(it)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Task: ")
                        Spacer(modifier = Modifier.width(16.dp))
                        SelectTask(
                            items = allTasks,
                            selected = selectedTask,
                            enabled = true
                        ) {
                            selectedTask = it
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // top row with date and navigation buttons
                    Column(modifier = Modifier.weight(1f)) {
                        LazyColumn(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()) {
                            items(allEntries?.size ?: 0) { entryIndex ->
                                val entry = allEntries!![entryIndex]
                                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween){
                                    var showDialog by remember { mutableStateOf(false) }
                                    if (showDialog) {
                                        val startInstant = Instant.ofEpochMilli(entry.startTime)
                                            .atZone(ZoneId.systemDefault())
                                        val endInstant = Instant.ofEpochMilli(entry.endTime)
                                            .atZone(ZoneId.systemDefault())
                                        EditEntryDialog(
                                            selectedDate = startInstant.toLocalDate(),
                                            selectedStartTime = startInstant.toLocalTime(),
                                            selectedEndTime = endInstant.toLocalTime(),
                                            selectedTask = allTasks?.filter { task: Task -> task.id == entry.taskId }
                                                ?.firstOrNull(),
                                            viewModel,
                                            onConfirm = { date: LocalDate, startTime: LocalTime, endTime: LocalTime, taskId: Long ->
                                                viewModel.updateEntry(
                                                    Entry(
                                                        LocalDateTime.of(date, startTime).toInstant(
                                                            ZoneId.systemDefault().rules.getOffset(
                                                                Instant.now()
                                                            )
                                                        ).toEpochMilli(),
                                                        LocalDateTime.of(date, endTime).toInstant(
                                                            ZoneId.systemDefault().rules.getOffset(
                                                                Instant.now()
                                                            )
                                                        ).toEpochMilli(),
                                                        taskId,
                                                        entry.id
                                                    )
                                                )
                                                showDialog = false
                                            }) { showDialog = false }
                                    }
                                    Column(modifier = Modifier.clickable { showDialog = true }) {
                                        Text(text = dateOfTimestamp(entry.startTime), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text(text = getFormattedTimeStr(entry.startTime, entry.endTime), fontSize = 14.sp)
                                    }
                                    Button(onClick = {
                                        viewModel.deleteEntry(entry.id)
                                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Icon(Icons.Filled.Delete, "Delete")
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total: " + allEntries?.let { it -> getTotalTime(it) })
                    }
                }
            }
        },

        bottomBar = { FooterNavigation(selectedTab = Tab.Overview, onTabSelected = {
            navController.navigate(it.screenName)
        })
        }
    )
}
