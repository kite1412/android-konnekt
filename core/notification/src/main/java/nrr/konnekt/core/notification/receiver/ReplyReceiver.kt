package nrr.konnekt.core.notification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.common.result.Result
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.MessageRepository
import nrr.konnekt.core.notification.R
import nrr.konnekt.core.notification.util.createIntentAction
import nrr.konnekt.core.notification.util.safeOnReceive
import javax.inject.Inject

private const val LOG_TAG = "ReplyReceiver"

@AndroidEntryPoint
internal class ReplyReceiver : BroadcastReceiver() {
    @Inject
    lateinit var messageRepository: MessageRepository
    @Inject
    lateinit var chatRepository: ChatRepository

    override fun onReceive(p0: Context?, p1: Intent?) {
        safeOnReceive(p0, p1) { context, intent ->
            val results = RemoteInput.getResultsFromIntent(intent)
            val replyText = results?.getCharSequence(EXTRA_TEXT_REPLY)?.toString()
            val chatId = intent.getStringExtra(EXTRA_CHAT_ID)

            if (replyText != null && chatId != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val res = messageRepository.sendMessage(
                        chatId = chatId,
                        content = replyText
                    )
                    if (res is Result.Success) {
                        Log.d(LOG_TAG, "Message sent successfully")
                    }
                }
            }
        }
    }

    companion object {
        val REPLY_ACTION = createIntentAction("REPLY_ACTION")
        const val EXTRA_TEXT_REPLY = "extra_text_reply"
        const val EXTRA_CHAT_ID = "extra_chat_id"

        fun createReplyNotificationAction(context: Context, chatId: String): NotificationCompat.Action {
            val remoteInput = RemoteInput.Builder(EXTRA_TEXT_REPLY)
                .setLabel("Reply")
                .build()
            val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
                action = REPLY_ACTION
                putExtra(EXTRA_CHAT_ID, chatId)
            }
            val replyPendingIntent = PendingIntent.getBroadcast(
                /*context = */context,
                /*requestCode = */chatId.hashCode(),
                /*intent = */replyIntent,
                /*flags = */PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            return NotificationCompat.Action.Builder(
                /*icon = */R.drawable.reply,
                /*title = */"Reply",
                /*intent = */replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()
        }
    }
}