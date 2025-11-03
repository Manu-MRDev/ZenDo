package mx.com.virtualhand.zendo.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import mx.com.virtualhand.zendo.domain.Task

class TaskRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val tasksCollection = firestore.collection("tasks")

    // 🔹 Escuchar en tiempo real los cambios en Firestore
    val tasksFlow: Flow<List<Task>> = callbackFlow {
        val listener = tasksCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Task::class.java)
            } ?: emptyList()

            trySend(tasks)
        }

        awaitClose { listener.remove() }
    }

    // 🔹 Agregar tarea
    suspend fun addTask(task: Task) {
        tasksCollection.document(task.id).set(task).await()
    }

    // 🔹 Eliminar tarea
    suspend fun removeTask(task: Task) {
        tasksCollection.document(task.id).delete().await()
    }

    // 🔹 Actualizar tarea
    suspend fun updateTask(task: Task) {
        tasksCollection.document(task.id).set(task).await()
    }
}

