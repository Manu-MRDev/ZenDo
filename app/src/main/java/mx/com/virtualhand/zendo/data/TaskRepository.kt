package mx.com.virtualhand.zendo.data

import androidx.compose.runtime.mutableStateListOf
import mx.com.virtualhand.zendo.domain.Task

class TaskRepository {

    // Lista mutable de tareas (simulación)
    val tasks = mutableStateListOf(
        Task(title = "Reunión de equipo", time = "09:00", category = "Trabajo"),
        Task(title = "Ejercicio", time = "18:00", category = "Salud"),
        Task(title = "Estudiar Kotlin", time = "20:00", category = "Estudio")
    )

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
    }

    fun updateTask(updatedTask: Task) {
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index >= 0) tasks[index] = updatedTask
    }
}

