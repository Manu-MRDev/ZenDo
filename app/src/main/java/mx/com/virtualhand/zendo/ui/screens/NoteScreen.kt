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
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Note
import mx.com.virtualhand.zendo.ui.components.AddNoteForm
import mx.com.virtualhand.zendo.ui.components.AddNoteButton
import mx.com.virtualhand.zendo.ui.components.NoteCard
import mx.com.virtualhand.zendo.ui.viewmodel.NoteViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesScreen(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    // 🔹 Observa el flujo de notas desde Firestore en tiempo real
    val notes by noteViewModel.notes.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showNoteDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        if (notes.isEmpty()) {
            Text(
                text = "No hay notas",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        onUpdate = {
                            noteToEdit = note
                            showNoteDialog = true
                        },
                        onDelete = {
                            // 🔹 Eliminación con corrutina (asincrónica)
                            scope.launch { noteViewModel.removeNote(note) }
                        }
                    )
                }
            }
        }

        // 🔹 Botón flotante para agregar notas
        AddNoteButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                noteToEdit = null
                showNoteDialog = true
            }
        )
    }

    // 🔹 Diálogo de agregar o editar nota
    if (showNoteDialog) {
        AddNoteForm(
            existingNote = noteToEdit,
            onDismiss = { showNoteDialog = false },
            onSave = { newNote ->
                scope.launch {
                    if (noteToEdit == null) {
                        noteViewModel.addNote(newNote)
                    } else {
                        noteViewModel.updateNote(newNote)
                    }
                    showNoteDialog = false
                }
            }
        )
    }
}
