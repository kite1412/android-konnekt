package nrr.konnekt.core.domain.model

import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.Message

data class LatestChatMessage(
    val chat: Chat,
    val message: Message
)
