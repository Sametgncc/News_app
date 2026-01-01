package com.example.read_app.core.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.syncDataStore by preferencesDataStore(name = "sync_prefs")

class SyncPrefs(private val context: Context) {
    // tercihlerimi yönetmek için kullandığım class
    private val KEY_LAST_SYNC = longPreferencesKey("last_sync_epoch_ms")
    private val KEY_IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")

    val lastSyncEpochMs: Flow<Long?> =
        context.syncDataStore.data.map { prefs -> prefs[KEY_LAST_SYNC] }

    val isDarkTheme: Flow<Boolean> =
        context.syncDataStore.data.map { prefs -> prefs[KEY_IS_DARK_THEME] ?: false }

    suspend fun setLastSync(epochMs: Long = System.currentTimeMillis()) {
        context.syncDataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC] = epochMs
        }
    }
    
    suspend fun setDarkTheme(isDark: Boolean) {
        context.syncDataStore.edit { prefs ->
            prefs[KEY_IS_DARK_THEME] = isDark
        }
    }
}
