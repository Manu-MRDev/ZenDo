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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.ui.components.AddTaskButton
import mx.com.virtualhand.zendo.ui.components.BottomNavigationBar
import mx.com.virtualhand.zendo.ui.components.MainTopBar
import mx.com.virtualhand.zendo.ui.components.TaskCard
import mx.com.virtualhand.zendo.ui.components.AddTaskForm
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenWithBottomNav(taskViewModel: TaskViewModel = viewModel()) {
    val navController: NavHostController = rememberNavController()
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Estados para el diálogo de tarea (compartido entre todas las pantallas)
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            MainTopBar(
                categories = tasks.map { it.category }.distinct(),
                onMenuItemClick = { /* Perfil, Configuración, Ayuda, Cerrar sesión */ },
                onFilterSelected = { /* Filtrado global */ },
                onDateSelected = { /* Selección de fecha global */ }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "agenda",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("agenda") {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (tasks.isEmpty()) {
                        Text(
                            text = "No hay tareas",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            items(tasks) { task ->
                                TaskCard(
                                    task = task,
                                    onCheckedChange = { done ->
                                        scope.launch {
                                            taskViewModel.updateTask(task.copy(done = done))
                                        }
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

                    // FAB solo en la pantalla de Agenda
                    AddTaskButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        onClick = {
                            taskToEdit = null
                            showTaskDialog = true
                        }
                    )
                }
            }

            composable("tareas") {
                TasksScreen(
                    taskViewModel = taskViewModel
                )
            }

            composable("categorias") {
                Text(text = "Pantalla Categorías")
            }

            composable("notas") {
                Text(text = "Pantalla Notas")
            }

            composable("timer") {
                Text(text = "Pantalla Timer")
            }
        }
    }

    // Diálogo compartido para agregar o editar tarea
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
