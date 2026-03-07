package nrr.konnekt.core.domain.util

import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User

fun Chat.isPersonalChatBlocked(user: User) =
    type == ChatType.PERSONAL &&
            participants
                .firstOrNull { participant ->
                    participant.user.id == user.id
                }
                ?.status
                ?.leftAt != null