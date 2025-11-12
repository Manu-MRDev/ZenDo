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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
                        onClick = {
                            onFilterSelected(category)
                            showCategoryMenu = false
                        }
                    )
                }
            }

            // Botón de calendario
            IconButton(onClick = { showCalendar = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Calendario")
            }
        }
    )

    // Menú lateral
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("Configuración") },
            onClick = { onMenuItemClick("Configuración"); showMenu = false }
        )
        DropdownMenuItem(
            text = { Text("Información") },
            onClick = { onMenuItemClick("Información"); showMenu = false }
        )
        DropdownMenuItem(
            text = { Text("Cerrar sesión") },
            onClick = { onMenuItemClick("Cerrar sesión"); showMenu = false }
        )
    }

    // 📅 Calendario real dentro del diálogo
    if (showCalendar) {
        val datePickerState = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        val selectedDate = selectedDateMillis?.let {
                            val localDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        } ?: "Sin selección"

                        onDateSelected(selectedDate)
                        showCalendar = false
                    }
                ) {
                    //Text("Seleccionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendar = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false // oculta el selector de año/mes
                )
            }
        )
    }
}
