package nrr.konnekt.core.notification.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Participant(
    val id: String,
    val name: String,
    @SerialName("image_path")
    val imagePath: String?,
    @SerialName("last_read_at")
    val lastReadAt: Instant?
)
