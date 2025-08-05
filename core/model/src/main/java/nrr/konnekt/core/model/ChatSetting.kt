package nrr.konnekt.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSetting(
    val name: String,
    @SerialName("icon_path")
    val iconPath: String? = null,
    val description: String? = null,
    @SerialName("permission_setting")
    val permissionSettings: ChatPermissionSettings? = null
)