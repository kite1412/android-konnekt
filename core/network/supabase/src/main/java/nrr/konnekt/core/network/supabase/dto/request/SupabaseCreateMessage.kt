package nrr.konnekt.core.network.supabase.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseCreateMessage(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("sender_id")
    val senderId: String,
    val content: String
)
