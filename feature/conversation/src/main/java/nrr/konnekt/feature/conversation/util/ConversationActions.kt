package nrr.konnekt.feature.conversation.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.ui.util.ChatDropdownItems

@Composable
internal fun ColumnScope.ConversationActions(
    blocked: Boolean,
    chatType: ChatType,
    dismiss: () -> Unit,
    onClearChat: () -> Unit,
    onBlockChange: (Boolean) -> Unit,
    onLeaveChat: () -> Unit
) {
    when (chatType) {
        ChatType.PERSONAL -> PersonalChatActions(
            blocked = blocked,
            dismiss = dismiss,
            onClearChat = onClearChat,
            onBlockChange = onBlockChange
        )
        ChatType.GROUP -> GroupChatActions(
            dismiss = dismiss,
            onClearChat = onClearChat,
            onLeaveChat = onLeaveChat
        )
        ChatType.CHAT_ROOM -> RoomChatActions(
            dismiss = dismiss,
            onLeaveChat = onLeaveChat
        )
    }
}

@Composable
private fun ColumnScope.PersonalChatActions(
    blocked: Boolean,
    dismiss: () -> Unit,
    onClearChat: () -> Unit,
    onBlockChange: (Boolean) -> Unit,
) {
    if (blocked) ChatDropdownItems.Unblock(
        dismiss = dismiss,
        onBlockChange = onBlockChange
    )
    ChatDropdownItems.ClearChat(
        dismiss = dismiss,
        onClearChat = onClearChat
    )
    if (!blocked) ChatDropdownItems.Block(
        dismiss = dismiss,
        onBlockChange = onBlockChange
    )
}

@Composable
private fun ColumnScope.GroupChatActions(
    dismiss: () -> Unit,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit
) {
    ChatDropdownItems.ClearChat(
        dismiss = dismiss,
        onClearChat = onClearChat
    )
    ChatDropdownItems.Leave(
        dismiss = dismiss,
        onLeaveChat = onLeaveChat
    )
}

@Composable
private fun ColumnScope.RoomChatActions(
    dismiss: () -> Unit,
    onLeaveChat: () -> Unit
) {
    ChatDropdownItems.Leave(
        dismiss = dismiss,
        onLeaveChat = onLeaveChat
    )
}