package ch.hslu.mobpro.timeismoney

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ch.hslu.mobpro.timeismoney.components.FooterNavigation
import ch.hslu.mobpro.timeismoney.components.Tab
import ch.hslu.mobpro.timeismoney.service.TimeService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.*

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        viewModel.deleteAllEntries()
                        Toast.makeText(context, "Alle Einträge gelöscht!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "Alle Einträge löschen")
                    }
                    Button(onClick = {
                        if (isServiceRunning(context, TimeService::class.java)) {
                            Toast.makeText(context, "Bitte stoppe zuerst den Timer!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.deleteAllEntries()
                            viewModel.deleteAllTasks()
                            Toast.makeText(context, "Alle Daten gelöscht!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(text = "Alle Daten löschen")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Angemeldet als: " + currentUser?.email)
                    Spacer(modifier = Modifier.height(4.dp))
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
        },

        bottomBar = { FooterNavigation(selectedTab = Tab.Settings, onTabSelected = {
            navController.navigate(it.screenName)
        })
        }
    )
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
