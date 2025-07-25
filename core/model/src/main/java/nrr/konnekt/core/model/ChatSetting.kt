package nrr.konnekt.core.model

data class ChatSetting(
    val name: String,
    val iconPath: String?,
    val description: String?,
    val permissionSetting: ChatPermissionSetting
)