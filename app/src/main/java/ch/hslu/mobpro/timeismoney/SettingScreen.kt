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
import ch.hslu.mobpro.timeismoney.components.CreateTaskDialog
import ch.hslu.mobpro.timeismoney.components.FooterNavigation
import ch.hslu.mobpro.timeismoney.components.SelectTask
import ch.hslu.mobpro.timeismoney.components.Tab
import ch.hslu.mobpro.timeismoney.room.TaskEntry
import ch.hslu.mobpro.timeismoney.service.TimeService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController, viewModel: MainViewModel) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    val context = LocalContext.current
    val serviceStoppedReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
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

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(it)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            val intent = Intent(context, TimeService::class.java)
                            context.stopService(intent)
                            auth.signOut()
                            navController.navigate("loginScreen")
                        }) {
                            Text(text = "Logout")
                        }
                    }
                }
            }
        },

        bottomBar = { FooterNavigation(selectedTab = Tab.Settings, onTabSelected = {
            navController.navigate(it.screenName)
        })
        }
    )
}
