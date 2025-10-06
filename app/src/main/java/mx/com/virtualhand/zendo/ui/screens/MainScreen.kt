package mx.com.virtualhand.zendo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.zendo.domain.Task
import mx.com.virtualhand.zendo.domain.TaskUseCase

// ----------------------
// MAIN SCREEN
// ----------------------
@Composable
fun MainScreen(taskUseCase: TaskUseCase) {
    val tasks = remember { mutableStateListOf<Task>().apply { addAll(taskUseCase.getTasks()) } }

    Scaffold(
        topBar = { MainTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: abrir formulario agregar tarea */ },
                containerColor = Color(0xFF00897B)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea", tint = Color.White)
            }
        },
        bottomBar = { MainBottomBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Hoy, 5 de octubre", style = MaterialTheme.typography.headlineSmall)
            Text("${tasks.count { !it.done }} tareas pendientes", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onCheckedChange = {
                            val index = tasks.indexOf(task)
                            tasks[index] = task.copy(done = !task.done)
                            taskUseCase.updateTask(tasks[index])
                        },
                        onDelete = {
                            tasks.remove(task)
                            taskUseCase.removeTask(task)
                        },
                        onEdit = { /* TODO: editar tarea */ }
                    )
                }
            }
        }
    }
}

// ----------------------
// TOP BAR
// ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    CenterAlignedTopAppBar(
        title = { Text("ZenDo") },
        actions = {
            IconButton(onClick = { /* filtrar */ }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
            }
            IconButton(onClick = { /* buscar */ }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
            IconButton(onClick = { /* calendario */ }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Calendario")
            }
            IconButton(onClick = { /* menú */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menú")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Gray
        )
    )
}


// ----------------------
// TASK CARD
// ----------------------
@Composable
fun TaskCard(task: Task, onCheckedChange: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = if (task.done) Color(0xFFF0F0F0) else Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = { onCheckedChange() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title)
                Text("${task.time} • ${task.category}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
        }
    }
}

// ----------------------
// BOTTOM NAVIGATION
// ----------------------
@Composable
fun MainBottomBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { /* Hoy */ },
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
            label = { Text("Hoy") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Tareas */ },
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Tareas") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Categorías */ },
            icon = { Icon(Icons.Default.Label, contentDescription = null) },
            label = { Text("Categorías") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Timer */ },
            icon = { Icon(Icons.Default.Timer, contentDescription = null) },
            label = { Text("Timer") }
        )
    }
}


