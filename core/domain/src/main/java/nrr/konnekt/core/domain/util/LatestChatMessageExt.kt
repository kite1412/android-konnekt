package nrr.konnekt.core.domain.util

import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import kotlin.time.Instant

fun LatestChatMessage.sentByCurrentUser(user: User) =
    message?.sender?.id == user.id

fun LatestChatMessage.unreadByCurrentUser(user: User) =
    message != null &&
            message.sender.id != user.id &&
            message.sentAt > (chat
                .participants
                .firstOrNull { participant ->
                    participant.user.id == user.id
                }
                ?.status
                ?.lastReadAt ?: Instant.DISTANT_FUTURE)

fun LatestChatMessage.deletedByCurrentUser(user: User) =
    message
        ?.messageStatuses
        ?.firstOrNull { status ->
            status.user.id == user.id
        }
        ?.isDeleted == true

fun LatestChatMessage.blockedByCurrentUser(user: User) =
    chat.type == ChatType.PERSONAL &&
            chat.participants
                .firstOrNull { participant ->
                    participant.user.id == user.id
                }
                ?.status
                ?.leftAt != null