package mx.com.virtualhand.zendo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.com.virtualhand.zendo.domain.Task

private val Context.dataStore by preferencesDataStore(name = "tasks_store")

class TaskRepository(private val context: Context) {

    private val TASKS_KEY = stringPreferencesKey("tasks_json")
    private val gson = Gson()

    // Obtener lista de tareas como Flow
    val tasksFlow: Flow<List<Task>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[TASKS_KEY]
            if (json.isNullOrEmpty()) emptyList()
            else gson.fromJson(json, object : TypeToken<List<Task>>() {}.type)
        }

    // Guardar lista completa
    suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { prefs ->
            prefs[TASKS_KEY] = gson.toJson(tasks)
        }
    }
}
