package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SupabaseChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    val role: String,
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("left_at")
    val leftAt: Instant?
)
