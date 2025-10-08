package mx.com.virtualhand.zendo.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import mx.com.virtualhand.zendo.data.TaskRepository

class TaskUseCase(private val repository: TaskRepository) {

    // Obtener lista de tareas como Flow
    val tasksFlow: Flow<List<Task>> = repository.tasksFlow

    // Agregar tarea
    suspend fun addTask(task: Task) {
        val currentTasks = repository.tasksFlow.first() // Obtener la lista actual
        repository.saveTasks(currentTasks + task)       // Guardar nueva lista
    }

    // Eliminar tarea
    suspend fun removeTask(task: Task) {
        val currentTasks = repository.tasksFlow.first()
        repository.saveTasks(currentTasks.filter { it.id != task.id })
    }

    // Actualizar tarea
    suspend fun updateTask(updatedTask: Task) {
        val currentTasks = repository.tasksFlow.first()
        val newList = currentTasks.map { if (it.id == updatedTask.id) updatedTask else it }
        repository.saveTasks(newList)
    }
}
