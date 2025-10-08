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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.ui.components.*
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val tasks by taskViewModel.tasks.collectAsState()

    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showCompletedTasks by remember { mutableStateOf(false) } // false = mostrar no completadas
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Filtrado
    val filteredTasks = tasks.filter { task ->
        val statusMatch = task.done == showCompletedTasks
        val categoryMatch = selectedCategory?.let { task.category == it } ?: true
        statusMatch && categoryMatch
    }

    Scaffold(
        topBar = {
            MainTopBar(
                categories = tasks.map { it.category }.distinct(),
                onMenuItemClick = { /* Perfil, Configuración, Ayuda, Cerrar sesión */ },
                onFilterSelected = { category -> selectedCategory = category },
                onDateSelected = { /* opcional */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            AddTaskButton(
                onClick = {
                    taskToEdit = null
                    showTaskDialog = true
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Botones de filtro arriba
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showCompletedTasks = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showCompletedTasks) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                    ) { Text("No completadas") }

                    Button(
                        onClick = { showCompletedTasks = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCompletedTasks) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                    ) { Text("Completadas") }
                }

                if (filteredTasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "No hay tareas",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredTasks) { task ->
                            TaskCard(
                                task = task,
                                onCheckedChange = { done ->
                                    scope.launch { taskViewModel.updateTask(task.copy(done = done)) }
                                },
                                onDelete = { scope.launch { taskViewModel.removeTask(task) } },
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
