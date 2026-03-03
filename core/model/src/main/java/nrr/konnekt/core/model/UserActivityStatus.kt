package nrr.konnekt.core.model

import kotlin.time.Instant

data class UserActivityStatus(
    val user: User,
    val lastActiveAt: Instant?
)