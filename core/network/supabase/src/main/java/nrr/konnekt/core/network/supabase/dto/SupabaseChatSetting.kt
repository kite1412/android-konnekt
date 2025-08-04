package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatSetting

@Serializable
internal data class SupabaseChatSetting(
    @SerialName("chat_id")
    val chatId: String,
    val name: String,
    val description: String?,
    @SerialName("icon_path")
    val iconPath: String?
)

internal fun ChatSetting.toSupabaseChatSetting(
    chatId: String
) = SupabaseChatSetting(
    chatId = chatId,
    name = name,
    description = description,
    iconPath = iconPath
)
