package nrr.konnekt.core.network.supabase.dto.response.rpc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import kotlin.time.Instant

@Serializable
internal data class SupabaseChatRpc(
    val id: String,
    val type: String,
    val setting: SupabaseChatSettingRpc,
    @SerialName("created_at")
    val createdAt: Instant,
    val participants: List<SupabaseChatParticipantRpc>
)

internal fun SupabaseChatRpc.toModel() = Chat(
    id = id,
    type = ChatType.valueOf(type.uppercase()),
    setting = setting.toModel(),
    createdAt = createdAt,
    participants = participants.map(SupabaseChatParticipantRpc::toModel)
)
