package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatParticipantStatus
import kotlin.time.Instant

@Serializable
internal data class SupabaseChatParticipantStatus(
    @SerialName("user_id")
    val userId: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("cleared_at")
    val clearedAt: Instant? = null,
    @SerialName("left_at")
    val leftAt: Instant? = null,
    @SerialName("archived_at")
    val archivedAt: Instant? = null,
    @SerialName("last_read_at")
    val lastReadAt: Instant? = null
)

internal fun SupabaseChatParticipantStatus.toModel() =
    ChatParticipantStatus(
        joinedAt = joinedAt,
        clearedAt = clearedAt,
        leftAt = leftAt,
        archivedAt = archivedAt,
        lastReadAt = lastReadAt
    )
