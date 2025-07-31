package nrr.konnekt.core.model

import kotlin.time.Instant

data class Event(
    val id: String,
    val createdBy: String,
    val chatId: String,
    val title: String,
    val description: String?,
    val createdAt: Instant,
    val editedAt: Instant,
    val startsAt: Instant
)