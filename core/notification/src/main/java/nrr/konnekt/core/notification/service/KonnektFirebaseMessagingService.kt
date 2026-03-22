package nrr.konnekt.core.notification.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.network.upload.util.CachingFileResolver
import nrr.konnekt.core.notification.NotificationManager
import nrr.konnekt.core.notification.dto.ChatLatestMessages
import nrr.konnekt.core.notification.util.ChatNotificationData
import nrr.konnekt.core.notification.util.getCircularBitmap
import nrr.konnekt.core.storage.datastore.PreferencesKeys
import nrr.konnekt.core.storage.datastore.getPreference
import nrr.konnekt.core.storage.datastore.setPreference
import javax.inject.Inject

private const val LOG_TAG = "KonnektFirebaseMessagingService"

@AndroidEntryPoint
internal class KonnektFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var authentication: Authentication
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
        if (notificationManager.isNotificationPermissionGranted(this))
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val data = ChatLatestMessages(message.data)
                    val currentUserId = getPreference(PreferencesKeys.CURRENT_USER_ID)
                    val notificationData = currentUserId?.let { currentUserId ->
                        ChatNotificationData(
                            data = data,
                            currentUserId = currentUserId
                        )
                    }

                    if (
                        getPreference(PreferencesKeys.DISABLED_CHAT_NOTIFICATION_IDS)
                            ?.contains(data.chat.id) != true
                    ) notificationData
                        ?.let(notificationManager::notifyNewMessages)
                        ?.let {
                            Log.d(LOG_TAG, "Notification sent for chat: ${notificationData.id}")
                        }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to parse chat latest messages")
                    e.printStackTrace()
                }
            }
    }

    private suspend fun ChatNotificationData(
        data: ChatLatestMessages,
        currentUserId: String
    ): ChatNotificationData? = if (data.latestMessages.isNotEmpty()) {
        val isGroup = data.chat.isGroup
        val firstOtherParticipant = if (!isGroup) data.firstOtherParticipantOrNull(currentUserId)
        else null
        val currentParticipant = data.currentParticipantOrNull(currentUserId)
        val sortedLatestMessages = data.latestMessages.sortedByDescending { it.sentAt }
        val lastOtherMessageIndex = sortedLatestMessages.indexOfLast {
            it.senderId != currentUserId
        }
        val latestMessages = sortedLatestMessages
            .run {
                if (lastOtherMessageIndex != -1) take(lastOtherMessageIndex)
                else this
            }
            .filter { latestMessage ->
                currentParticipant?.lastReadAt?.let {
                    latestMessage.sentAt > it
                } ?: true
            }
            .map { latestMessage ->
                latestMessage.toMessageData(cache)
            }

        ChatNotificationData(
            id = data.chat.id,
            name = if (isGroup) data.chat.name ?: "Group"
            else firstOtherParticipant?.name ?: "Person",
            isGroup = data.chat.isGroup,
            icon = cache.getCircularBitmap(
                if (isGroup) data.chat.iconPath
                else firstOtherParticipant?.imagePath
            ),
            messages = latestMessages
        )
    } else null

    private fun ChatLatestMessages.firstOtherParticipantOrNull(currentUserId: String) =
        chat.participants.firstOrNull { participant ->
            participant.id != currentUserId
        }

    private fun ChatLatestMessages.currentParticipantOrNull(currentUserId: String) =
        chat.participants.firstOrNull { participant ->
            participant.id == currentUserId
        }
}