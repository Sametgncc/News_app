package com.example.read_app.core.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.syncDataStore by preferencesDataStore(name = "sync_prefs")

class SyncPrefs(private val context: Context) {

    private val KEY_LAST_SYNC = longPreferencesKey("last_sync_epoch_ms")

    val lastSyncEpochMs: Flow<Long?> =
        context.syncDataStore.data.map { prefs -> prefs[KEY_LAST_SYNC] }

    suspend fun setLastSync(epochMs: Long = System.currentTimeMillis()) {
        context.syncDataStore.edit { prefs ->
            prefs[KEY_LAST_SYNC] = epochMs
        }
    }
}
