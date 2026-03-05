package nrr.konnekt.core.network.supabase.dto.response.rpc.model

import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipantStatus
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toModel

@Serializable
internal data class SupabaseChatParticipantRpc(
    val role: String,
    val user: SupabaseUser,
    val status: SupabaseChatParticipantStatus
)

internal fun SupabaseChatParticipantRpc.toModel() = ChatParticipant(
    role = ParticipantRole.valueOf(role.uppercase()),
    user = user.toModel(),
    status = status.toModel()
)