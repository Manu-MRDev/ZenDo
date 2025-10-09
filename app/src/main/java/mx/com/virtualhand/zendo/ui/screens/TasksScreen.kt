package mx.com.virtualhand.zendo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.ui.components.AddTaskButton
import mx.com.virtualhand.zendo.ui.components.AddTaskForm
import mx.com.virtualhand.zendo.ui.components.TaskCard
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksScreen(taskViewModel: TaskViewModel) {
    val scope = rememberCoroutineScope()
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())

    // Estado del filtro: true = completadas, false = no completadas
    var showCompleted by remember { mutableStateOf(false) }

    // Estado para diálogo de agregar/editar
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    val filteredTasks = tasks.filter { it.done == showCompleted }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showCompleted = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("No Completadas")
                }
                Button(
                    onClick = { showCompleted = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Completadas")
                }
            }
        },
        floatingActionButton = {
            // FAB solo en TasksScreen
            AddTaskButton(
                onClick = {
                    taskToEdit = null
                    showTaskDialog = true
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (filteredTasks.isEmpty()) {
                    Text(
                        text = "No hay tareas",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        items(filteredTasks) { task ->
                            TaskCard(
                                task = task,
                                onCheckedChange = { done ->
                                    scope.launch { taskViewModel.updateTask(task.copy(done = done)) }
                                },
                                onDelete = {
                                    scope.launch { taskViewModel.removeTask(task) }
                                },
                                onUpdate = {
                                    taskToEdit = task
                                    showTaskDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo de agregar/editar tarea en TasksScreen
    if (showTaskDialog) {
        AddTaskForm(
            onDismiss = { showTaskDialog = false },
            onSave = { newTask ->
                if (taskToEdit == null) {
                    taskViewModel.addTask(newTask)
                } else {
                    taskViewModel.updateTask(newTask)
                }
                showTaskDialog = false
            },
            existingTask = taskToEdit
        )
    }
}
