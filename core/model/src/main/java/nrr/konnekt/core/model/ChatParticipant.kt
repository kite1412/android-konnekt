package nrr.konnekt.core.model

import kotlin.time.Instant

data class ChatParticipant(
    val chatId: String,
    val userId: String,
    val role: ParticipantRole,
    val joinedAt: Instant,
    val leftAt: Instant?
)