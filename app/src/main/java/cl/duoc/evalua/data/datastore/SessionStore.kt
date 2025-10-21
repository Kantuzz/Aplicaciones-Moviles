package cl.duoc.evalua.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {
    private val LOGGED_IN = booleanPreferencesKey("logged_in")
    private val EMAIL = stringPreferencesKey("email")

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[LOGGED_IN] ?: false }
    val email: Flow<String> = context.dataStore.data.map { it[EMAIL] ?: "" }

    suspend fun saveLogin(email: String) {
        context.dataStore.edit {
            it[LOGGED_IN] = true
            it[EMAIL] = email
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[LOGGED_IN] = false
            it[EMAIL] = ""
        }
    }
}