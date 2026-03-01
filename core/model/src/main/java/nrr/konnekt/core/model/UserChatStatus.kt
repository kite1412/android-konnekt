package nrr.konnekt.core.model

import kotlin.time.Instant

data class UserChatStatus(
    val joinedAt: Instant,
    val clearedAt: Instant?,
    val leftAt: Instant?,
    val archivedAt: Instant?,
    val lastReadAt: Instant?
)
