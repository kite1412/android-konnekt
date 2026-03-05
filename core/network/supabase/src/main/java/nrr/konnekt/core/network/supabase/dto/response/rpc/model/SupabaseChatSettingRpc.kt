package nrr.konnekt.core.network.supabase.dto.response.rpc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.response.toModel

@Serializable
internal data class SupabaseChatSettingRpc(
    @SerialName("chat_id")
    val chatId: String,
    val name: String,
    val description: String? = null,
    @SerialName("icon_path")
    val iconPath: String? = null,
    @SerialName("permission_settings")
    val permissionSettings: SupabaseChatPermissionSettings? = null
)

internal fun SupabaseChatSettingRpc.toModel() = ChatSetting(
    name = name,
    iconPath = iconPath,
    description = description,
    permissionSettings = permissionSettings?.toModel()
)