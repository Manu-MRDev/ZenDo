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
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.ui.components.*
import mx.com.virtualhand.zendo.ui.viewmodel.NoteViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel
import mx.com.virtualhand.zendo.ui.viewmodel.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenWithBottomNav(
    taskViewModel: TaskViewModel,
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    onLogout: () -> Unit // 🔹 Callback para cerrar sesión
) {
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Estado para el diálogo de tarea
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Estado de filtrado por categoría
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Lista filtrada según categoría seleccionada
    val filteredTasks = selectedCategory?.let { category ->
        tasks.filter { it.category == category }
    } ?: tasks

    // TimerViewModel
    val timerViewModel: TimerViewModel = viewModel()

    Scaffold(
        topBar = {
            MainTopBar(
                categories = tasks.map { it.category }.distinct(),
                onMenuItemClick = { menuItem ->
                    when (menuItem) {
                        "Cerrar sesión" -> onLogout() // 🔹 Cierra sesión
                        else -> { /* otras opciones */ }
                    }
                },
                onFilterSelected = { category ->
                    selectedCategory = category
                },
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

                    // FAB para agregar tarea
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
                TasksScreen(taskViewModel = taskViewModel)
            }

            composable("categorias") {
                CategoriesScreen(taskViewModel = taskViewModel)
            }

            composable("notas") {
                NotesScreen(noteViewModel = noteViewModel, navController = navController)
            }

            composable("timer") {
                TimerScreen(timerViewModel = timerViewModel)
            }
        }
    }

    // ----- DIÁLOGO COMPARTIDO DE TAREA -----
    if (showTaskDialog) {
        AddTaskForm(
            onDismiss = { showTaskDialog = false },
            onSave = { newTask ->
                if (taskToEdit == null) taskViewModel.addTask(newTask)
                else taskViewModel.updateTask(newTask)
                showTaskDialog = false
            },
            existingTask = taskToEdit
        )
    }
}

