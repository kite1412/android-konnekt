package nrr.konnekt.core.network.supabase.dto.response.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipantStatus
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toModel

@Serializable
internal data class GetChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    val user: SupabaseUser,
    val role: String,
    val status: SupabaseChatParticipantStatus
)

internal fun GetChatParticipant.toChatParticipant() =
    ChatParticipant(
        user = user.toModel(),
        role = ParticipantRole.valueOf(role.uppercase()),
        status = status.toModel()
    )