package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class User(
    val id: String,
    val email: String,
    val username: String,
    val bio: String? = null,
    @SerialName("image_path")
    val imagePath: String? = null,
    @SerialName("created_at")
    @Contextual
    val createdAt: Instant
)