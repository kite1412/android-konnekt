package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class UserStatus(
    @SerialName("user_id")
    val userId: String,
    @SerialName("last_active_at")
    @Contextual
    val lastActiveAt: Instant
)

fun User.updateUserStatus() = UserStatus(
    userId = id,
    lastActiveAt = Clock.System.now()
)