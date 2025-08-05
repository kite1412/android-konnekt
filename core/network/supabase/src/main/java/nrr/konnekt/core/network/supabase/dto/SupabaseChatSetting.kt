package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.model.ChatPermissionSettings
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

internal fun CreateChatSetting.toSupabaseChatSetting(
    chatId: String
) = SupabaseChatSetting(
    chatId = chatId,
    name = name,
    description = description,
    iconPath = null
)

internal fun SupabaseChatSetting.toChatSetting(
    permissionSettings: ChatPermissionSettings? = null,
    iconPath: String? = null
) = ChatSetting(
    name = name,
    description = description,
    iconPath = iconPath,
    permissionSettings = permissionSettings
)