package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserActivityStatus
import kotlin.time.Instant

@Serializable
internal data class SupabaseUserActivityStatus(
    @SerialName("user_id")
    val userId: String,
    @SerialName("last_active_at")
    val lastActiveAt: Instant?
)

internal fun SupabaseUserActivityStatus.toModel(user: User) =
    UserActivityStatus(
        user = user,
        lastActiveAt = lastActiveAt
    )