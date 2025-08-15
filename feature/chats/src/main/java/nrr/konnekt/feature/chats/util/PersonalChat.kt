package nrr.konnekt.feature.chats.util

import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now

internal fun User.createTempPersonalChat() = Chat(
    id = "",
    type = ChatType.PERSONAL,
    createdAt = now(),
    setting = ChatSetting(
        name = username,
        iconPath = imagePath
    )
)