package nrr.konnekt.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageStatus(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false
)