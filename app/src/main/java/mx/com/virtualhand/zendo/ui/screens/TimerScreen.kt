package mx.com.virtualhand.zendo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.com.virtualhand.zendo.ui.viewmodel.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(timerViewModel: TimerViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val timeLeft by timerViewModel.timeLeft.collectAsState()
    val isRunning by timerViewModel.isRunning.collectAsState()
    val currentPhase by timerViewModel.currentPhase.collectAsState()

    val completedSessions by timerViewModel.completedSessions.collectAsState()
    val totalTimeToday by timerViewModel.totalTimeToday.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = currentPhase,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = formatTime(timeLeft),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                if (isRunning) timerViewModel.pauseTimer()
                else timerViewModel.startTimer(context)
            }) {
                Text(if (isRunning) "Pausa" else "Comenzar")
            }

            Button(onClick = { timerViewModel.resetTimer() }) {
                Text("Reiniciar")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sección de estadísticas de hoy
        Column(horizontalAlignment = Alignment.Start) {
            Text("Sesiones completadas: $completedSessions")
            Text("Tiempo total hoy: ${formatTime(totalTimeToday)}")
        }
    }
}

// Helper para mostrar mm:ss
fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}
