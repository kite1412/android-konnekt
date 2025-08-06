package nrr.konnekt.core.domain.model

import nrr.konnekt.core.model.Chat

data class LatestChatMessage(
    val chat: Chat,
    val messageDetail: MessageDetail? = null
)
