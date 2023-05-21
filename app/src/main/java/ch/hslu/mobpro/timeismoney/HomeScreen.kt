package ch.hslu.mobpro.timeismoney

import android.app.ActivityManager
import android.app.DatePickerDialog
import android.content.*
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
import ch.hslu.mobpro.timeismoney.MainActivity.Companion.EXTRA_TASK
import ch.hslu.mobpro.timeismoney.MainActivity.Companion.EXTRA_TASK_ID
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
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val pref: SharedPreferences =
        context.getApplicationContext().getSharedPreferences(currentUser?.uid ?: "StandardUser", 0)

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val allEntries by viewModel.allEntries.observeAsState()
    val allTasks by viewModel.allTasks.observeAsState()
    var selectedTask by remember { mutableStateOf(allTasks?.filter { task: Task -> task.id == pref.getLong("lastTask", -1) }?.firstOrNull()) }

    var isTimerRunning by remember { mutableStateOf(isServiceRunning(context, TimeService::class.java)) }
    LaunchedEffect(key1 = Unit) {
        viewModel.setUserId(currentUser?.uid ?: "")
        selectedDate = LocalDate.now()
        selectedTask = allTasks?.filter { task: Task -> task.id == pref.getLong("lastTask", -1) }?.firstOrNull()
    }
    LaunchedEffect(isTimerRunning) {
        val intent = Intent(context, TimeService::class.java)
        if (isTimerRunning) {
            intent.putExtra(EXTRA_TASK, selectedTask?.title)
            intent.putExtra(EXTRA_TASK_ID, selectedTask?.id)
            context.startService(intent)
        } else {
            context.stopService(intent)
        }
    }
    val serviceStoppedReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isTimerRunning = false
                val startTime = intent?.getLongExtra("startTime", -1) ?: -1
                val endTime = intent?.getLongExtra("endTime", -1) ?: -1
                val taskId = intent?.getLongExtra("taskId", -1) ?: -1
                viewModel.addEntry(startTime, endTime, taskId)
                println("Added entry: " + taskId)
            }
        }
    }
    DisposableEffect(Unit) {
        val intentFilter = IntentFilter(TimeService.ACTION_SERVICE_STOPPED)
        context.registerReceiver(serviceStoppedReceiver, intentFilter)

        onDispose {
            context.unregisterReceiver(serviceStoppedReceiver)
        }
    }
    LaunchedEffect(key1 = selectedTask) {
        var editor = pref.edit()
        editor.putLong("lastTask", selectedTask?.id ?: -1)
        editor.commit()
    }

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(it)) {
                    // top row with date and navigation buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Vorheriger Tag")
                        }
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
                        IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Nächster Tag")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 16.dp, end = 16.dp)) {
                        LazyColumn() {
                            items(allEntries?.filter { taskEntry ->
                                isTimestampOnDate(taskEntry.startTime, selectedDate)
                            }?.size ?: 0) { entryIndex ->
                                val entry = allEntries!![entryIndex]
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    var showDialog by remember { mutableStateOf(false) }
                                    if (showDialog) {
                                        val startInstant = Instant.ofEpochMilli(entry.startTime).atZone(ZoneId.systemDefault())
                                        val endInstant = Instant.ofEpochMilli(entry.endTime).atZone(ZoneId.systemDefault())
                                        EditEntryDialog(
                                            selectedDate = startInstant.toLocalDate(),
                                            selectedStartTime = startInstant.toLocalTime(),
                                            selectedEndTime= endInstant.toLocalTime(),
                                            selectedTask = allTasks?.filter { task: Task -> task.id == entry.taskId }?.firstOrNull(),
                                            viewModel,
                                            onConfirm = { date: LocalDate, startTime: LocalTime, endTime: LocalTime, taskId: Long ->
                                                viewModel.updateEntry(
                                                    Entry(
                                                        LocalDateTime.of(date, startTime).toInstant(
                                                            ZoneId.systemDefault().rules.getOffset(Instant.now())
                                                        ).toEpochMilli(),
                                                        LocalDateTime.of(date, endTime).toInstant(
                                                            ZoneId.systemDefault().rules.getOffset(Instant.now())
                                                        ).toEpochMilli(),
                                                        taskId,
                                                        entry.id
                                                    )
                                                )
                                                showDialog = false
                                            }) { showDialog = false }
                                    }
                                    Column(modifier = Modifier.clickable { showDialog = true }) {
                                        Text(
                                            text = entry.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = getFormattedTimeStr(
                                                entry.startTime,
                                                entry.endTime
                                            ), fontSize = 14.sp
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.deleteEntry(entry.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
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
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total: " + allEntries?.filter { taskEntry -> isTimestampOnDate(taskEntry.startTime, selectedDate) }
                            ?.let { it -> getTotalTime(it) })
                        var showDialog by remember { mutableStateOf(false) }
                        if (showDialog) {
                            CreateEntryDialog(
                                selectedDate,
                                viewModel,
                                onConfirm = { date: LocalDate, startTime: LocalTime, endTime: LocalTime, taskId: Long ->
                                    viewModel.addEntry(
                                        LocalDateTime.of(date, startTime).toInstant(
                                            ZoneId.systemDefault().rules.getOffset(Instant.now())
                                        ).toEpochMilli(),
                                        LocalDateTime.of(date, endTime).toInstant(
                                            ZoneId.systemDefault().rules.getOffset(Instant.now())
                                        ).toEpochMilli(),
                                        taskId
                                    )
                                    showDialog = false
                                }) { showDialog = false }
                        }
                        Button(onClick = {
                            showDialog = true
                        }) {
                            Text(text = "Manuell erfassen")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var showDialog by remember { mutableStateOf(false) }
                        SelectTask(
                            items = allTasks,
                            selected = selectedTask,
                            enabled = !isTimerRunning
                        ) {
                            selectedTask = it
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = {
                            showDialog = true
                        }) {
                            Text(text = "+")
                        }

                        if (showDialog) {
                            CreateTaskDialog({
                                viewModel.addTask(it)
                                showDialog = false
                            }) { showDialog = false }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            enabled = selectedTask != null
                        ) {
                            Icon(if (isTimerRunning) Icons.Filled.Done else Icons.Filled.PlayArrow, "Start")
                        }
                    }
                }
            }
        },

        bottomBar = { FooterNavigation(selectedTab = Tab.Home, onTabSelected = {
            navController.navigate(it.screenName)
        })}
    )
}

fun isTimestampOnDate(timestamp: Long, date: LocalDate): Boolean {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val dateTimeDate = dateTime.toLocalDate()
    return dateTimeDate == date
}
fun dateOfTimestamp(timestamp: Long): String{
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
    return dateTime.format(formatter)
}

fun getFormattedTimeStr(startTime: Long, endTime: Long): String {
    val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault())
    val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val duration = Duration.between(startDateTime, endDateTime)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    return startDateTime.format(formatter) + " - " + endDateTime.format(formatter) + " (" + String.format("%02dh %02dm", hours, minutes) + ")"
}

fun getTotalTime(entries: List<TaskEntry>): String {
    var duration = 0L
    entries.forEach {
        val startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.startTime), ZoneId.systemDefault())
        val endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.endTime), ZoneId.systemDefault())
        duration += Duration.between(startDateTime, endDateTime).toMinutes()
    }

    val hours = floor(duration.toDouble() / 60).toInt()
    val minutes = duration % 60
    return String.format("%02dh %02dm", hours, minutes)
}

private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}