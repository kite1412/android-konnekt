package nrr.konnekt.core.notification.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import nrr.konnekt.core.notification.R
import nrr.konnekt.core.notification.receiver.ReplyReceiver

private const val MAIN_ACTIVITY_NAME = "nrr.konnekt.MainActivity"
private const val SCHEME = "konnekt://"

internal sealed class KonnektNotification(
    val channelId: String,
    val channelName: String,
    val channelDescription: String,
    val importance: Int
) {
    object Messages : KonnektNotification(
        channelId = "message",
        channelName = "Messages",
        channelDescription = "Message notifications",
        importance = NotificationManager.IMPORTANCE_HIGH
    ) {
        const val SCHEME_HOST = "$SCHEME/messages"
        const val DEEP_LINK_CHAT_ID_KEY = "chatId"
        const val DEEP_LINK_URI_PATTERN = "$SCHEME_HOST/$DEEP_LINK_CHAT_ID_KEY"

        fun createNotification(
            context: Context,
            currentPerson: Person,
            chat: ChatNotificationData
        ): Notification {
            val chatId = chat.id

            return createNotification(
                context = context,
                contentIntent = createContentIntent(
                    context = context,
                    requestCode = chatId.hashCode(),
                    data = "$SCHEME_HOST/${chatId}".toUri()
                )
            ) {
                chat.icon?.let { icon ->
                    setSmallIcon(IconCompat.createWithBitmap(icon))
                } ?: setSmallIcon(
                    if (chat.isGroup) R.drawable.person_2
                    else R.drawable.person
                )

                val messages = mutableListOf<NotificationCompat.MessagingStyle.Message>()
                val style = NotificationCompat.MessagingStyle(currentPerson)
                    .setConversationTitle(chat.name)
                    .setGroupConversation(chat.isGroup)

                chat.messages
                    .sortedBy { it.sentAt }
                    .forEach { message ->
                        messages.add(
                            NotificationCompat.MessagingStyle.Message(
                                /*text = */message.message,
                                /*timestamp = */message.sentAt.toEpochMilliseconds(),
                                /*person = */message.sender
                            )
                        )
                    }
                style.messages.addAll(messages)

                setStyle(style)

                val action = ReplyReceiver.createReplyNotificationAction(
                    context = context,
                    chatId = chatId
                )
                addAction(action)
            }
        }
    }

    object ChatInvitations : KonnektNotification(
        channelId = "chat_invitation",
        channelName = "Chat Invitations",
        channelDescription = "Chat invitation notifications",
        importance = NotificationManager.IMPORTANCE_HIGH
    )

    internal fun createContentIntent(
        context: Context,
        requestCode: Int,
        data: Uri? = null
    ): PendingIntent = PendingIntent.getActivity(
        /*context = */context,
        /*requestCode = */requestCode,
        /*intent = */Intent().apply {
            action = Intent.ACTION_VIEW
            this.data = data
            component = ComponentName(
                context.packageName,
                MAIN_ACTIVITY_NAME
            )
        },
        /*flags = */PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    internal fun createNotification(
        context: Context,
        contentIntent: PendingIntent,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification {
        ensureChannelExists(context)

        return NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.logo_small)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .apply(block)
            .build()
    }

    private fun ensureChannelExists(context: Context) {
        val channel = NotificationChannel(
            /*id = */channelId,
            /*name = */channelName,
            /*importance = */importance
        ).apply {
            description = channelDescription
        }

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}