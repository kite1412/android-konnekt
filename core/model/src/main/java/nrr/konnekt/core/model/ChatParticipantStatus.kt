package nrr.konnekt.core.model

import kotlin.time.Instant

data class ChatParticipantStatus(
    val joinedAt: Instant,
    val clearedAt: Instant? = null,
    val leftAt: Instant? = null,
    val archivedAt: Instant? = null,
    val lastReadAt: Instant? = null
)
