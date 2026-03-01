package nrr.konnekt.core.model

data class ChatSetting(
    val name: String,
    val iconPath: String? = null,
    val description: String? = null,
    val permissionSettings: ChatPermissionSettings? = null
)