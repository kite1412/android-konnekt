package nrr.konnekt.core.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("konnekt_datastore")

fun <T> Context.observePreference(key: Preferences.Key<T>): Flow<T?> =
    dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            it[key]
        }

suspend fun <T> Context.setPreference(key: Preferences.Key<T>, value: T): Boolean =
    try {
        dataStore.edit {
            it[key] = value
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

suspend fun <T> Context.removePreference(key: Preferences.Key<T>): Boolean =
    try {
        dataStore.edit {
            it.remove(key)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

suspend fun <T> Context.getPreference(key: Preferences.Key<T>): T? =
    dataStore.data.firstOrNull()?.get(key)