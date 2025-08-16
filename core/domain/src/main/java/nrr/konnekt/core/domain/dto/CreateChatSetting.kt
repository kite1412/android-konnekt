package nrr.konnekt.core.domain.dto

import nrr.konnekt.core.model.ChatPermissionSettings

data class CreateChatSetting(
    val name: String,
    val description: String? = null,
    val icon: FileUpload? = null,
    val permissionSettings: ChatPermissionSettings? = null
)