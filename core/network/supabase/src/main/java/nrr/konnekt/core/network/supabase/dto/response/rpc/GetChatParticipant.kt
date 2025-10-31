package nrr.konnekt.core.network.supabase.dto.response.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toUser
import kotlin.time.Instant

@Serializable
internal data class GetChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    val user: SupabaseUser,
    val role: String,
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("left_at")
    val leftAt: Instant?
)

internal fun GetChatParticipant.toChatParticipant() =
    ChatParticipant(
        chatId = chatId,
        user = user.toUser(),
        role = ParticipantRole.valueOf(role.uppercase()),
        joinedAt = joinedAt,
        leftAt = leftAt
    )