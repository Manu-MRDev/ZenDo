package mx.com.virtualhand.zendo.ui.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class TimerViewModel : ViewModel() {

    private val pomodoroTime = 25 * 60
    private val shortBreakTime = 5 * 60
    private val longBreakTime = 15 * 60

    private var cycleCount = 0

    private val _timeLeft = MutableStateFlow(pomodoroTime)
    val timeLeft: StateFlow<Int> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentPhase = MutableStateFlow("Pomodoro")
    val currentPhase: StateFlow<String> = _currentPhase

    private val _completedSessions = MutableStateFlow(0)
    val completedSessions: StateFlow<Int> = _completedSessions

    private val _totalTimeToday = MutableStateFlow(0)
    val totalTimeToday: StateFlow<Int> = _totalTimeToday

    private var timerJobRunning = false

    fun startTimer(context: Context) {
        if (_isRunning.value) return
        _isRunning.value = true

        if (!timerJobRunning) {
            timerJobRunning = true
            viewModelScope.launch {
                while (_isRunning.value && _timeLeft.value > 0) {
                    delay(1000)
                    _timeLeft.value -= 1
                    _totalTimeToday.value += 1

                    if (_timeLeft.value == 0) {
                        onPhaseComplete(context)
                    }
                }
                timerJobRunning = false
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
    }

    fun resetTimer() {
        _isRunning.value = false
        _timeLeft.value = when (_currentPhase.value) {
            "Pomodoro" -> pomodoroTime
            "Descanso" -> shortBreakTime
            "Descanso largo" -> longBreakTime
            else -> pomodoroTime
        }
    }

    private fun onPhaseComplete(context: Context) {
        when (_currentPhase.value) {
            "Pomodoro" -> {
                cycleCount++
                _completedSessions.value += 1
                _currentPhase.value = if (cycleCount % 4 == 0) "Descanso largo" else "Descanso"
                _timeLeft.value = if (_currentPhase.value == "Descanso largo") longBreakTime else shortBreakTime
            }
            else -> {
                _currentPhase.value = "Pomodoro"
                _timeLeft.value = pomodoroTime
            }
        }

        sendNotification(context)
    }

    private fun sendNotification(context: Context) {
        val channelId = "pomodoro_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pomodoro Timer", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Pomodoro")
            .setContentText("Fase completa: ${_currentPhase.value}")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(1, notification)
    }
}
