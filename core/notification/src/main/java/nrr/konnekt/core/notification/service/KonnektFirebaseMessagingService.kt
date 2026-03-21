package nrr.konnekt.core.notification.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.network.upload.util.CachingFileResolver
import nrr.konnekt.core.notification.NotificationManager
import nrr.konnekt.core.notification.dto.ChatLatestMessages
import nrr.konnekt.core.notification.util.ChatNotificationData
import nrr.konnekt.core.notification.util.getCircularBitmap
import nrr.konnekt.core.storage.datastore.PreferencesKeys
import nrr.konnekt.core.storage.datastore.setPreference
import javax.inject.Inject

private const val LOG_TAG = "KonnektFirebaseMessagingService"

@AndroidEntryPoint
class KonnektFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var authentication: Authentication
    @Inject
    lateinit var chatRepository: ChatRepository
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var cache: CachingFileResolver

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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val data = ChatLatestMessages(message.data)
                val res = chatRepository.getChatById(data.chatId)

                if (res is Result.Success) {
                    val resData = res.data
                    val currentUser = authentication.getLoggedInUserOrNull()
                    val readMarker = data.participantReadMarkers.firstOrNull { readMarker ->
                        readMarker.userId == currentUser?.id
                    }
                    val sortedLatestMessages = data.latestMessages.sortedByDescending { it.sentAt }
                    val lastOtherMessageIndex = sortedLatestMessages.indexOfLast {
                        it.senderId != currentUser?.id
                    }
                    val latestMessages = sortedLatestMessages
                        .run {
                            if (lastOtherMessageIndex != -1) take(lastOtherMessageIndex)
                            else this
                        }
                        .filter { latestMessage ->
                            readMarker?.lastReadAt?.let {
                                latestMessage.sentAt > it
                            } ?: true
                        }
                        .map { latestMessage ->
                            latestMessage.toMessageData(cache)
                        }

                    if (latestMessages.isNotEmpty()) notificationManager.notifyNewMessages(
                        data = ChatNotificationData(
                            id = resData.id,
                            name = resData.name(),
                            isGroup = resData.type != ChatType.PERSONAL,
                            icon = cache.getCircularBitmap(resData.setting?.iconPath),
                            messages = latestMessages
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to parse chat latest messages")
                e.printStackTrace()
            }
        }
    }
}