package nrr.konnekt.core.domain.util

import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.User
import kotlin.time.Instant

fun LatestChatMessage.isUnreadByCurrentUser(user: User) =
    message != null &&
            message.sender.id != user.id &&
            message.sentAt > (chat
                .participants
                .firstOrNull { participant ->
                    participant.user.id == user.id
                }
                ?.status
                ?.lastReadAt ?: Instant.DISTANT_FUTURE)