package nrr.konnekt.core.notification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.model.UpdateChatParticipantStatus
import nrr.konnekt.core.domain.model.UpdateStatus
import nrr.konnekt.core.domain.usecase.UpdateChatParticipantStatusUseCase
import nrr.konnekt.core.notification.R
import nrr.konnekt.core.notification.util.cancelNotification
import nrr.konnekt.core.notification.util.createIntentAction
import nrr.konnekt.core.notification.util.safeOnReceive
import javax.inject.Inject

private const val LOG_TAG = "MarkAsReadReceiver"

@AndroidEntryPoint
internal class MarkAsReadReceiver : BroadcastReceiver() {
    @Inject
    lateinit var updateChatParticipantStatusUseCase: UpdateChatParticipantStatusUseCase

    override fun onReceive(p0: Context?, p1: Intent?) {
        safeOnReceive(p0, p1) { context, intent ->
            intent.getStringExtra(EXTRA_CHAT_ID)?.let { chatId ->
                CoroutineScope(Dispatchers.Default).launch {
                    updateChatParticipantStatusUseCase(
                        update = UpdateChatParticipantStatus(
                            chatId = chatId,
                            updateLastReadAt = UpdateStatus()
                        )
                    )
                    context.cancelNotification(chatId.hashCode())
                    Log.d(LOG_TAG, "marked as read: $chatId")
                }
            }
        }
    }

    companion object {
        val MARK_AS_READ_ACTION = createIntentAction("MARK_AS_READ_ACTION")
        const val EXTRA_CHAT_ID = "extra_chat_id"

        fun createMarkAsReadAction(context: Context, chatId: String): NotificationCompat.Action {
            val intent = Intent(context, MarkAsReadReceiver::class.java).apply {
                action = MARK_AS_READ_ACTION
                putExtra(EXTRA_CHAT_ID, chatId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                /*context = */context,
                /*requestCode = */chatId.hashCode(),
                /*intent = */intent,
                /*flags = */PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            return NotificationCompat.Action.Builder(
                /*icon = */R.drawable.message_circle_check,
                /*title = */"Mark as read",
                /*intent = */pendingIntent
            ).build()
        }
    }
}