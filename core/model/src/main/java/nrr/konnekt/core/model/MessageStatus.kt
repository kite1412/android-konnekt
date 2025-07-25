package nrr.konnekt.core.model

import kotlin.time.Instant

data class MessageStatus(
    val messageId: String,
    val userId: String,
    val readAt: Instant,
    val isDeleted: Boolean
)