package mx.com.virtualhand.zendo.domain

import mx.com.virtualhand.zendo.data.TaskRepository

class TaskUseCase(private val repository: TaskRepository) {

    fun getTasks() = repository.tasks
    fun addTask(task: Task) = repository.addTask(task)
    fun removeTask(task: Task) = repository.removeTask(task)
    fun updateTask(task: Task) = repository.updateTask(task)
}