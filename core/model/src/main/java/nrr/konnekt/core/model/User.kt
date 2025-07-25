package nrr.konnekt.core.model

import kotlin.time.Instant

data class User(
    val id: String,
    val username: String,
    val bio: String?,
    val createdAt: Instant
)