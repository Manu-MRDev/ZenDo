package mx.com.virtualhand.zendo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mx.com.virtualhand.zendo.ui.components.MainTopBar
import mx.com.virtualhand.zendo.ui.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoriesScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    // Estadísticas
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.done }
    val pendingTasks = totalTasks - completedTasks

    // Categorías y conteo
    val categoriesCount = tasks.groupingBy { it.category }.eachCount()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sección de estadísticas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Estadísticas", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total de tareas: $totalTasks", fontSize = 16.sp)
                Text("Tareas completadas: $completedTasks", fontSize = 16.sp)
                Text("Tareas pendientes: $pendingTasks", fontSize = 16.sp)
            }
        }

        // Sección de categorías
        Text("Categorías", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        categoriesCount.forEach { (category, count) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(category, style = MaterialTheme.typography.bodyLarge)
                    Text("$count tareas", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (categoriesCount.isEmpty()) {
            Text(
                text = "No hay categorías disponibles",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
