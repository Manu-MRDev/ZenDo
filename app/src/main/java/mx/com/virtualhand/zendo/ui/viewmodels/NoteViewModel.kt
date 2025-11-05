package mx.com.virtualhand.zendo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.com.virtualhand.zendo.domain.Note
import mx.com.virtualhand.zendo.domain.NoteUseCase
import mx.com.virtualhand.zendo.data.NoteRepository

class NoteViewModel : ViewModel() {

    private val repository = NoteRepository()
    private val useCase = NoteUseCase(repository)

    // Estado de la lista de notas
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        // Suscribirse al Flow de notas desde NoteUseCase
        viewModelScope.launch {
            useCase.notesFlow.collect { noteList ->
                _notes.value = noteList
            }
        }
    }

    // Agregar nota
    fun addNote(note: Note) {
        viewModelScope.launch {
            useCase.addNote(note)
        }
    }

    // Eliminar nota
    fun removeNote(note: Note) {
        viewModelScope.launch {
            useCase.removeNote(note)
        }
    }

    // Actualizar nota
    fun updateNote(note: Note) {
        viewModelScope.launch {
            useCase.updateNote(note)
        }
    }
}
