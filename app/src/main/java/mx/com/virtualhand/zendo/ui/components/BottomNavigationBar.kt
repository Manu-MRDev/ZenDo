package mx.com.virtualhand.zendo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class BottomMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    menuItems: List<BottomMenuItem> = listOf(
        BottomMenuItem("Agenda", Icons.Default.CalendarToday, "agenda"),
        BottomMenuItem("Tareas", Icons.Default.Checklist, "tareas"),
        BottomMenuItem("Categorías", Icons.Default.Label, "categorias"),
        BottomMenuItem("Notas", Icons.Default.Note, "notas"),
        BottomMenuItem("Timer", Icons.Default.Timer, "timer")
    ),
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(menuItems.first().route) }

    NavigationBar(modifier = modifier) {
        menuItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == item.route,
                onClick = {
                    if (selectedItem != item.route) { // Evita navegar a la misma ruta
                        selectedItem = item.route
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            // Esto limpia la pila para evitar crashes por múltiples instancias
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                }
            )
        }
    }
}

