package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.User
import kotlin.time.Instant

@Serializable
internal data class SupabaseUser(
    val id: String,
    val username: String,
    val email: String,
    val bio: String?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("image_path")
    val imagePath: String?
)

internal fun SupabaseUser.toUser() =
    User(
        id = id,
        username = username,
        email = email,
        bio = bio,
        createdAt = createdAt,
        imagePath = imagePath
    )