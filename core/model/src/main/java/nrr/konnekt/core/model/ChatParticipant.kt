package nrr.konnekt.core.model

data class ChatParticipant(
    val user: User,
    val role: ParticipantRole,
    val status: ChatParticipantStatus
)