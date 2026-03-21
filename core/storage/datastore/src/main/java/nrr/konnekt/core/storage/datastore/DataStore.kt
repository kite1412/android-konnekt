package nrr.konnekt.core.storage.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("konnekt_datastore")