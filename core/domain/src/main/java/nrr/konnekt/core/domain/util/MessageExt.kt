package nrr.konnekt.core.domain.util

import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User

fun Message.isSentByCurrentUser(user: User) =
    sender.id == user.id

fun Message.isDeletedByCurrentUser(user: User) =
    messageStatuses
        .firstOrNull { status ->
            status.user.id == user.id
        }
        ?.isDeleted == true