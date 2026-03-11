package nrr.konnekt.core.network.supabase.dto.response.rpc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toModel
import kotlin.time.Instant

@Serializable
internal data class SupabaseChatInvitationRpc(
    val id: String,
    val chat: SupabaseChatRpc,
    val inviter: SupabaseUser,
    val receiver: SupabaseUser,
    @SerialName("invited_at")
    val invitedAt: Instant
)

internal fun SupabaseChatInvitationRpc.toModel() =
    ChatInvitation(
        id = id,
        chat = chat.toModel(),
        inviter = inviter.toModel(),
        receiver = receiver.toModel(),
        invitedAt = invitedAt
    )
