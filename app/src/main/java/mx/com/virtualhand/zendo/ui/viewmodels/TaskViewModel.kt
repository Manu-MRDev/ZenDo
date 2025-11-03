package mx.com.virtualhand.zendo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.data.TaskRepository

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()

    // Estado de la lista de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        // 🔹 Suscribirse a los cambios en Firestore
        viewModelScope.launch {
            repository.tasksFlow.collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    // 🔹 Agregar tarea
    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    // 🔹 Eliminar tarea
    fun removeTask(task: Task) {
        viewModelScope.launch {
            repository.removeTask(task)
        }
    }

    // 🔹 Actualizar tarea
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}

