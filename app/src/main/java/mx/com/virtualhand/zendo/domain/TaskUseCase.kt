package mx.com.virtualhand.zendo.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import mx.com.virtualhand.zendo.data.TaskRepository

class TaskUseCase(private val repository: TaskRepository) {

    // Obtener lista de tareas como Flow
    val tasksFlow: Flow<List<Task>> = repository.tasksFlow

    // Agregar tarea
    suspend fun addTask(task: Task) {
        repository.addTask(task) // Antes: saveTasks
    }

    // Eliminar tarea
    suspend fun removeTask(task: Task) {
        repository.removeTask(task) // Antes: saveTasks
    }

    // Actualizar tarea
    suspend fun updateTask(updatedTask: Task) {
        repository.updateTask(updatedTask) // Antes: saveTasks
    }
}