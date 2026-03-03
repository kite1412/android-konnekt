package nrr.konnekt.core.domain.model

import nrr.konnekt.core.model.ChatParticipant

data class UserChatParticipation(
    val chatId: String,
    val participation: ChatParticipant
)
