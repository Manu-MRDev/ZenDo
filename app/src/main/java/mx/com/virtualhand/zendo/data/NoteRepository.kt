package mx.com.virtualhand.zendo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.com.virtualhand.zendo.domain.Note

private val Context.dataStore by preferencesDataStore(name = "notes_store")

class NoteRepository(private val context: Context) {

    private val NOTES_KEY = stringPreferencesKey("notes_json")
    private val gson = Gson()

    // Obtener lista de notas como Flow
    val notesFlow: Flow<List<Note>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[NOTES_KEY]
            if (json.isNullOrEmpty()) emptyList()
            else gson.fromJson(json, object : TypeToken<List<Note>>() {}.type)
        }

    // Guardar lista completa
    suspend fun saveNotes(notes: List<Note>) {
        context.dataStore.edit { prefs ->
            prefs[NOTES_KEY] = gson.toJson(notes)
        }
    }
}
