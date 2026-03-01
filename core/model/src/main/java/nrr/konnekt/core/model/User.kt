package nrr.konnekt.core.model

import kotlin.time.Instant

data class User(
    val id: String,
    val email: String,
    val username: String,
    val bio: String? = null,
    val imagePath: String? = null,
    val createdAt: Instant
)