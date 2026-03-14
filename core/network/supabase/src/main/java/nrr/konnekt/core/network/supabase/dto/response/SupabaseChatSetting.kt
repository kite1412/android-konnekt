package nrr.konnekt.core.network.supabase.dto.response

import io.github.jan.supabase.realtime.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.domain.dto.ChatSettingEdit
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
) {
    companion object {
        val PrimaryKey = PrimaryKey<SupabaseChatSetting>(columnName = "chat_id") {
            it.chatId
        }
    }
}

internal fun ChatSettingEdit.toSupabaseChatSetting(
    chatId: String,
    iconPath: String? = null
) = SupabaseChatSetting(
    chatId = chatId,
    name = name,
    description = description,
    iconPath = iconPath
)

internal fun SupabaseChatSetting.toChatSetting(
    permissionSettings: ChatPermissionSettings? = null
) = ChatSetting(
    name = name,
    description = description,
    iconPath = iconPath,
    permissionSettings = permissionSettings
)