package nrr.konnekt.core.network.supabase.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    val role: String
)
