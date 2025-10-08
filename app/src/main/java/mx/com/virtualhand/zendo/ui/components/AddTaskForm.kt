package mx.com.virtualhand.zendo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.zendo.domain.Task

@Composable
fun AddTaskForm(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    existingTask: Task? = null // <- Si viene un task, estamos editando
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var time by remember { mutableStateOf(existingTask?.time ?: "") }
    var date by remember { mutableStateOf(existingTask?.date ?: "") }
    var category by remember { mutableStateOf(existingTask?.category ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingTask == null) "Agregar Tarea" else "Editar Tarea") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Hora (HH:MM)") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && category.isNotBlank()) {
                        val newTask = if (existingTask != null) {
                            existingTask.copy(
                                title = title,
                                time = time,
                                date = date,
                                category = category
                            )
                        } else {
                            Task(title = title, time = time, date = date, category = category)
                        }
                        onSave(newTask)
                        onDismiss()
                    }
                }
            ) {
                Text(if (existingTask == null) "Guardar" else "Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
