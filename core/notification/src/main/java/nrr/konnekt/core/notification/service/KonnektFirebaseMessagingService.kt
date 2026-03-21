package nrr.konnekt.core.notification.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.storage.datastore.PreferencesKeys
import nrr.konnekt.core.storage.datastore.setPreference
import javax.inject.Inject

private const val LOG_TAG = "KonnektFirebaseMessagingService"

@AndroidEntryPoint
class KonnektFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var authentication: Authentication

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

            val res = authentication.storeFcmToken(token)
            if (res is Result.Success && res.data) {
                Log.d(LOG_TAG, "Stored FCM token successfully")
            } else {
                Log.e(LOG_TAG, "Failed to store FCM token")
            }
        }
    }
}