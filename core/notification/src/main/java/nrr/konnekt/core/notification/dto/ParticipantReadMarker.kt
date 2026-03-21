package nrr.konnekt.core.notification.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
internal data class ParticipantReadMarker(
    @SerialName("user_id")
    val userId: String,
    @SerialName("last_read_at")
    val lastReadAt: Instant?
)
