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
import ch.hslu.mobpro.timeismoney.room.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun CreateEntryDialog(
    startDate: LocalDate,
    viewModel: MainViewModel,
    onConfirm: (LocalDate, LocalTime, LocalTime, Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(startDate) }
    var selectedStartTime by remember { mutableStateOf(LocalTime.now().minusHours(1)) }
    var selectedEndTime by remember { mutableStateOf(LocalTime.now()) }

    EditEntryDialog(
        "Eintrag erfassen",
        selectedDate,
        selectedStartTime,
        selectedEndTime,
        selectedTask = null,
        viewModel,
        onConfirm,
        onDismiss
    )
}