package nrr.konnekt.feature.chats.util

import nrr.konnekt.core.domain.dto.FileUpload

internal data class CreateGroupChatSetting(
    val icon: FileUpload? = null,
    val name: String = ""
)
