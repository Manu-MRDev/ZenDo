package mx.com.virtualhand.zendo.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import mx.com.virtualhand.zendo.domain.Note

class NoteRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")

    // 🔹 Escuchar en tiempo real los cambios en Firestore
    val notesFlow: Flow<List<Note>> = callbackFlow {
        val listener = notesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val notes = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Note::class.java)
            } ?: emptyList()

            trySend(notes)
        }

        awaitClose { listener.remove() }
    }

    // 🔹 Agregar nota
    suspend fun addNote(note: Note) {
        notesCollection.document(note.id).set(note).await()
    }

    // 🔹 Eliminar nota
    suspend fun removeNote(note: Note) {
        notesCollection.document(note.id).delete().await()
    }

    // 🔹 Actualizar nota
    suspend fun updateNote(note: Note) {
        notesCollection.document(note.id).set(note).await()
    }
}
