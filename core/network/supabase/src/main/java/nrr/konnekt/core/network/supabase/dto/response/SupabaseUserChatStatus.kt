package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SupabaseUserChatStatus(
    @SerialName("user_id")
    val userId: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("cleared_at")
    val clearedAt: Instant?,
    @SerialName("left_at")
    val leftAt: Instant?,
    @SerialName("archived_at")
    val archivedAt: Instant?,
    @SerialName("last_read_at")
    val lastReadAt: Instant?
)
