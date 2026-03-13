package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import kotlin.time.Instant

@Serializable
internal data class SupabaseChat(
    val id: String,
    val type: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant?
)

internal fun SupabaseChat.toChat(setting: ChatSetting? = null) =
    Chat(
        id = id,
        type = ChatType.valueOf(type.uppercase()),
        createdAt = createdAt,
        deletedAt = deletedAt,
        setting = setting
    )
