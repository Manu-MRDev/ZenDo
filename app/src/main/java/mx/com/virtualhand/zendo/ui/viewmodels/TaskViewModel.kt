package mx.com.virtualhand.zendo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.domain.TaskUseCase
import mx.com.virtualhand.zendo.data.TaskRepository

class TaskViewModel(context: Context) : ViewModel() {

    private val repository = TaskRepository(context)
    private val useCase = TaskUseCase(repository)

    // Estado de la lista de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        // Suscribirse al Flow de tareas desde TaskUseCase
        viewModelScope.launch {
            useCase.tasksFlow.collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    // Agregar tarea
    fun addTask(task: Task) {
        viewModelScope.launch {
            useCase.addTask(task)
        }
    }

    // Eliminar tarea
    fun removeTask(task: Task) {
        viewModelScope.launch {
            useCase.removeTask(task)
        }
    }

    // Actualizar tarea
    fun updateTask(task: Task) {
        viewModelScope.launch {
            useCase.updateTask(task)
        }
    }
}
