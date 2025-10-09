package mx.com.virtualhand.zendo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.com.virtualhand.zendo.domain.Note

@Composable
fun AddNoteForm(
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit,
    existingNote: Note? = null
) {
    var title by remember { mutableStateOf(existingNote?.title ?: "") }
    var description by remember { mutableStateOf(existingNote?.description ?: "") }
    var date by remember { mutableStateOf(existingNote?.date ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingNote == null) "Agregar Nota" else "Editar Nota") },
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.height(100.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (YYYY-MM-DD)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        val newNote = if (existingNote != null) {
                            existingNote.copy(title = title, description = description, date = date)
                        } else {
                            Note(title = title, description = description, date = date)
                        }
                        onSave(newNote)
                        onDismiss()
                    }
                }
            ) { Text(if (existingNote == null) "Guardar" else "Actualizar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
