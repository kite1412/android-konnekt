package nrr.konnekt.core.notification.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.storage.datastore.PreferencesKeys
import nrr.konnekt.core.storage.datastore.setPreference

private const val LOG_TAG = "KonnektFirebaseMessagingService"

class KonnektFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.Default).launch {
            if (
                setPreference(
                    key = PreferencesKeys.FCM_TOKEN,
                    value = token
                )
            ) Log.d(LOG_TAG, "New FCM token: $token")
            else Log.e(LOG_TAG, "Failed to set FCM token")
        }
    }
}