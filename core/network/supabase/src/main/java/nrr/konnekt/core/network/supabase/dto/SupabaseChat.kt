package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.Contextual
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
    @Contextual
    val createdAt: Instant
)

internal fun SupabaseChat.toChat(setting: ChatSetting?) =
    Chat(
        id = id,
        type = ChatType.valueOf(type.uppercase()),
        createdAt = createdAt,
        setting = setting
    )
