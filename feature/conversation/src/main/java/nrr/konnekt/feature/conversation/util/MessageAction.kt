package nrr.konnekt.feature.conversation.util

import nrr.konnekt.core.model.Message

internal data class MessageAction(
    val message: Message,
    val type: ActionType
)

internal enum class ActionType {
    SHOW_ACTIONS,
    FOCUS_ATTACHMENTS
}