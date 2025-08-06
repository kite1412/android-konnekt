package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class MessageStatus(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("read_at")
    @Contextual
    val readAt: Instant?,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false
)