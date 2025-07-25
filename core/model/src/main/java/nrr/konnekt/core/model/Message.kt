package nrr.konnekt.core.model

import kotlin.time.Instant

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val sentAt: Instant,
    val editedAt: Instant,
    val isHidden: Boolean,
    val attachments: List<Attachment> = emptyList()
)