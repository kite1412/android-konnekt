package nrr.konnekt.core.storage.datastore

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val FCM_TOKEN = stringPreferencesKey("fcm_token")
    val DISABLED_CHAT_NOTIFICATION_IDS = stringSetPreferencesKey("disabled_chat_notification_ids")
}