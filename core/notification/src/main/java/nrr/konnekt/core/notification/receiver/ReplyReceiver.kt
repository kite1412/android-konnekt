package nrr.konnekt.core.notification.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import nrr.konnekt.core.notification.R
import nrr.konnekt.core.notification.util.safeOnReceive

internal class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        safeOnReceive(p0, p1) { context, intent ->
            val results = RemoteInput.getResultsFromIntent(intent)
            val replyText = results?.getCharSequence(EXTRA_TEXT_REPLY)?.toString()
            val chatId = intent.getStringExtra(EXTRA_CHAT_ID)

            if (replyText != null && chatId != null) {
                Log.d("ReplyReceiver", "onReceive: $replyText")
            }
        }
    }

    companion object {
        const val EXTRA_TEXT_REPLY = "extra_text_reply"
        const val EXTRA_CHAT_ID = "extra_chat_id"

        fun createReplyNotificationAction(context: Context, chatId: String): NotificationCompat.Action {
            val remoteInput = RemoteInput.Builder(EXTRA_TEXT_REPLY)
                .setLabel("Reply")
                .build()
            val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
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