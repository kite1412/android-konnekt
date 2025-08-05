package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.Message
import kotlin.time.Instant

@Serializable
internal data class SupabaseMessage(
    val id: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("sender_id")
    val senderId: String,
    val content: String,
    @SerialName("sent_at")
    val sentAt: Instant,
    @SerialName("edited_at")
    val editedAt: Instant,
    @SerialName("is_hidden")
    val isHidden: Boolean
)

internal fun SupabaseMessage.toMessage() =
    Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        content = content,
        sentAt = sentAt,
        editedAt = editedAt,
        isHidden = isHidden
    )