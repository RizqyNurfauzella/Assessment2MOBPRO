package org.d3if3074.assessment2.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "music_settings_preference"
)

class SettingsDataStore(private val context: Context) {

    companion object {
        private val IS_GRID = booleanPreferencesKey("is_grid")
    }

    val layoutFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_GRID] ?: true
    }

    suspend fun saveLayout(isGrid: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_GRID] = isGrid
        }
    }
}
