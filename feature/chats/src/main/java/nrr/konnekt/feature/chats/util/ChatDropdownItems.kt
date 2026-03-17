package nrr.konnekt.feature.chats.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import nrr.konnekt.core.ui.util.ChatDropdownItems

@Composable
internal fun ColumnScope.PersonDropdownItems(
    archived: Boolean,
    blocked: Boolean,
    dismiss: () -> Unit,
    onArchive: () -> Unit,
    onClearChat: () -> Unit,
    onBlockChange: (blocked: Boolean) -> Unit
) {
    if (blocked) ChatDropdownItems.Unblock(
        dismiss = dismiss,
        onBlockChange = onBlockChange
    )
    CommonDropdownItems(
        archived = archived,
        dismiss = dismiss,
        onArchive = onArchive,
        onClearChat = onClearChat
    )
    if (!blocked) ChatDropdownItems.Block(
        dismiss = dismiss,
        onBlockChange = onBlockChange
    )
}

@Composable
internal fun ColumnScope.GroupDropdownItems(
    hasLeft: Boolean,
    dismiss: () -> Unit,
    onArchive: () -> Unit,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit
) {
    CommonDropdownItems(
        archived = false,
        dismiss = dismiss,
        onArchive = onArchive,
        onClearChat = onClearChat
    )
    if (!hasLeft) ChatDropdownItems.Leave(
        dismiss = dismiss,
        onLeaveChat = onLeaveChat
    )
}

@Composable
private fun ColumnScope.CommonDropdownItems(
    archived: Boolean,
    dismiss: () -> Unit,
    onArchive: () -> Unit,
    onClearChat: () -> Unit
) {
    if (!archived) ChatDropdownItems.Archive(
        dismiss = dismiss,
        onArchive = onArchive
    )
    ChatDropdownItems.ClearChat(
        dismiss = dismiss,
        onClearChat = onClearChat
    )
}