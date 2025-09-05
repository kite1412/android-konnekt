package nrr.konnekt.core.model

import kotlin.time.Instant

data class UserReadMarker(
    val user: User,
    val chatId: String,
    val lastReadAt: Instant
)
