package nrr.konnekt.core.domain.util

import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User

fun Chat.isPersonalChatBlocked(user: User) =
    type == ChatType.PERSONAL &&
            hasLeft(user)

fun Chat.hasLeftByCurrentUser(user: User) =
    type != ChatType.PERSONAL &&
            hasLeft(user)

fun Chat.isDeleted() = deletedAt != null

fun Chat.name() =
    setting?.name ?: ""

private fun Chat.hasLeft(user: User) =
    participants
        .firstOrNull { participant ->
            participant.user.id == user.id
        }
        ?.status
        ?.leftAt != null