package nrr.konnekt.core.model

import kotlin.time.Instant

// TODO fully migrate to UserChatStatus
data class UserReadMarker(
    val user: User,
    val chatId: String,
    val lastReadAt: Instant
)
