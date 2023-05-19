package ch.hslu.mobpro.timeismoney

import ch.hslu.mobpro.timeismoney.service.TimeService
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
import ch.hslu.mobpro.timeismoney.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var entries by remember { mutableStateOf(emptyList<String>()) }
    var showDialog by remember { mutableStateOf(false) }

    var taskEntries by remember { mutableStateOf(listOf("Rasen mähen", "Pflanzen giessen")) }
    var selectedItem by remember { mutableStateOf("Rasen mähen") }

    val context = LocalContext.current
    var isTimerRunning by remember { mutableStateOf(false) }
    val serviceStoppedReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isTimerRunning = false
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
    LaunchedEffect(isTimerRunning) {
        val intent = Intent(context, TimeService::class.java)
        if (isTimerRunning) {
            intent.putExtra(EXTRA_TASK, selectedItem)
            context.startService(intent)
        } else {
            context.stopService(intent)
        }
    }

    BackHandler(enabled = isTimerRunning) {
        isTimerRunning = false
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
                    Column(modifier = Modifier.weight(1f)) {
                        LazyColumn(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()) {
                            items(entries.size) { entry ->
                                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween){
                                    Column() {
                                        Text(text = entries[entry], fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text(text = "XX:XX - XX:XX (XXh XXm)", fontSize = 14.sp)
                                    }
                                    Button(onClick = {
                                        var changedEntries = entries.toMutableList()
                                        changedEntries.removeAt(entry)
                                        entries = changedEntries
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
                            .fillMaxWidth()
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SelectBox(items = taskEntries, selectedItem) {
                            selectedItem = it
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = {
                            showDialog = true
                        }) {
                            Text(text = "+")
                        }

                        if (showDialog) {
                            CreateEntryDialog({
                                taskEntries = taskEntries + it
                                showDialog = false
                            }) { showDialog = false }
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
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
