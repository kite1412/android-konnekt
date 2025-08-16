package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ParticipantRole
import kotlin.time.Instant

@Serializable
internal data class SupabaseChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    val role: String,
    @SerialName("joined_at")
    @Contextual
    val joinedAt: Instant,
    @SerialName("left_at")
    @Contextual
    val leftAt: Instant?
)

internal fun SupabaseChatParticipant.toChatParticipant() =
    ChatParticipant(
        chatId = chatId,
        userId = userId,
        role = ParticipantRole.valueOf(role.uppercase()),
        joinedAt = joinedAt,
        leftAt = leftAt
    )