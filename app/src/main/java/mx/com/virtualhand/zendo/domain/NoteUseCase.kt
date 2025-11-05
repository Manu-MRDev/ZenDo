package mx.com.virtualhand.zendo.domain

import kotlinx.coroutines.flow.Flow
import mx.com.virtualhand.zendo.data.NoteRepository

class NoteUseCase(private val repository: NoteRepository) {

    // 🔹 Flujo en tiempo real de las notas (sincronizado con Firestore)
    val notesFlow: Flow<List<Note>> = repository.notesFlow

    // 🔹 Agregar nota
    suspend fun addNote(note: Note) {
        repository.addNote(note)
    }

    // 🔹 Eliminar nota
    suspend fun removeNote(note: Note) {
        repository.removeNote(note)
    }

    // 🔹 Actualizar nota
    suspend fun updateNote(note: Note) {
        repository.updateNote(note)
    }
}
