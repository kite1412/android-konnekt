package nrr.konnekt.core.domain.dto

import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import kotlin.time.Instant

data class ChatSettingEdit(
    val name: String,
    val description: String? = null,
    val icon: FileUpload? = null,
    val deletedAt: Instant? = null,
    val permissionSettings: ChatPermissionSettings? = null
)

fun ChatSettingEdit.toChatSetting(iconPath: String? = null) = ChatSetting(
    name = name,
    description = description,
    iconPath = iconPath,
    permissionSettings = permissionSettings
)