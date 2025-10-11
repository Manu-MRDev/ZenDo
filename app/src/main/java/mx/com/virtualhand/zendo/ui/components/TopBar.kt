package mx.com.virtualhand.zendo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    onMenuItemClick: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onDateSelected: (String) -> Unit,
    categories: List<String> = emptyList()
) {
    var showMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("ZenDo") },
        navigationIcon = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.Menu, contentDescription = "Menú principal")
            }
        },
        actions = {
            // Filtro de categorías
            IconButton(onClick = { showCategoryMenu = !showCategoryMenu }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrar tareas")
            }
            DropdownMenu(
                expanded = showCategoryMenu,
                onDismissRequest = { showCategoryMenu = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = { onFilterSelected(category); showCategoryMenu = false }
                    )
                }
            }

            // Calendario
            IconButton(onClick = { showCalendar = !showCalendar }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
            }
        }
    )

    // Menú hamburguesa
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(text = { Text("Perfil") }, onClick = { onMenuItemClick("Perfil"); showMenu = false })
        // 🔹 "Configuración" abrirá el diálogo de selección de tema
        DropdownMenuItem(text = { Text("Configuración") }, onClick = { onMenuItemClick("Configuración"); showMenu = false })
        DropdownMenuItem(text = { Text("Información") }, onClick = { onMenuItemClick("Información"); showMenu = false })
        DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = { onMenuItemClick("Cerrar sesión"); showMenu = false })
    }

    // Calendario simple con AlertDialog
    if (showCalendar) {
        AlertDialog(
            onDismissRequest = { showCalendar = false },
            title = { Text("Seleccionar fecha") },
            text = { Text("Aquí se desplegará un calendario real") },
            confirmButton = {
                TextButton(onClick = { onDateSelected("2025-10-08"); showCalendar = false }) {
                    Text("Seleccionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendar = false }) { Text("Cancelar") }
            }
        )
    }
}
