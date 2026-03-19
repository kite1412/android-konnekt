package nrr.konnekt.core.notification.util

import android.graphics.Bitmap
import androidx.core.app.Person
import kotlin.time.Instant

data class ChatNotificationData(
    val id: String,
    val name: String,
    val isGroup: Boolean,
    val icon: Bitmap?,
    val messages: List<MessageData>
)

data class MessageData(
    val sender: Person,
    val message: String,
    val sentAt: Instant
)