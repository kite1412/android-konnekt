package nrr.konnekt.core.network.supabase.dto.response.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toModel
import kotlin.time.Instant

// TODO update model to include ChatParticipant.status
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
        user = user.toModel(),
        role = ParticipantRole.valueOf(role.uppercase()),
        status = ChatParticipantStatus(
            joinedAt = joinedAt,
            leftAt = leftAt
        )
    )