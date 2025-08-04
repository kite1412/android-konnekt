package nrr.konnekt.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSetting(
    val name: String,
    @SerialName("icon_path")
    val iconPath: String?,
    val description: String?,
    @SerialName("permission_setting")
    val permissionSetting: ChatPermissionSetting
)