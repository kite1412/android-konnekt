package nrr.konnekt.feature.conversation.util

import kotlinx.datetime.LocalDate
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.util.currentLocalDateTime
import nrr.konnekt.feature.conversation.util.ConversationItem.DateHeader
import nrr.konnekt.feature.conversation.util.ConversationItem.MessageItem

internal sealed interface ConversationItem {
    val key: String

    data class DateHeader(val date: LocalDate) : ConversationItem {
        override val key = "date-header-$date"
    }
    data class MessageItem(
        val message: Message,
        val wasSentByPreviousUser: Boolean
    ) : ConversationItem {
        override val key = "message-item-${message.id}"
    }
}

// this only works well with LazyColumn.reverseLayout = true
internal fun List<Message>.mapToConversationItem(): List<ConversationItem> =
    groupBy { it.sentAt.currentLocalDateTime().date }
        .toSortedMap(compareByDescending { it })
        .flatMap { (date, messages) ->
            val sorted = messages.sortedByDescending { it.sentAt }

            sorted.mapIndexed { i, m ->
                MessageItem(
                    message = m,
                    wasSentByPreviousUser = i + 1 < sorted.size &&
                        sorted[i + 1].sender.id == m.sender.id
                )
            } + listOf(DateHeader(date))
        }