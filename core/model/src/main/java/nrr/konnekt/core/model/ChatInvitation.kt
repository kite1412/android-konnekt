package nrr.konnekt.core.model

import kotlin.time.Instant

data class ChatInvitation(
    val id: String,
    val chat: Chat,
    val inviter: User,
    val receiver: User,
    val invitedAt: Instant,
    val canceledAt: Instant? = null,
    val acceptedAt: Instant? = null
)
