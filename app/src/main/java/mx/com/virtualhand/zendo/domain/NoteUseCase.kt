package mx.com.virtualhand.zendo.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import mx.com.virtualhand.zendo.data.NoteRepository
import mx.com.virtualhand.zendo.domain.Note

class NoteUseCase(private val repository: NoteRepository) {

    // Obtener lista de notas como Flow
    val notesFlow: Flow<List<Note>> = repository.notesFlow

    // Agregar nota
    suspend fun addNote(note: Note) {
        val currentNotes = repository.notesFlow.first()  // Obtener la lista actual
        repository.saveNotes(currentNotes + note)        // Guardar nueva lista
    }

    // Eliminar nota
    suspend fun removeNote(note: Note) {
        val currentNotes = repository.notesFlow.first()
        repository.saveNotes(currentNotes.filter { it.id != note.id })
    }

    // Actualizar nota
    suspend fun updateNote(updatedNote: Note) {
        val currentNotes = repository.notesFlow.first()
        val newList = currentNotes.map { if (it.id == updatedNote.id) updatedNote else it }
        repository.saveNotes(newList)
    }
}
