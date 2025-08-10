package nrr.konnekt.feature.chats.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.ui.component.DropdownItem

private val destructiveActionColor = Red

val clickWrapper = {
    dismiss: () -> Unit,
    action: () -> Unit
    ->
    {
        dismiss()
        action()
    }
}

@Composable
internal fun ColumnScope.PersonDropdownItems(
    archived: Boolean,
    blocked: Boolean,
    dismiss: () -> Unit,
    onArchive: () -> Unit,
    onClearChat: () -> Unit,
    onBlockChange: (blocked: Boolean) -> Unit
) {
    if (blocked) DropdownItem(
        text = "Unblock",
        onClick = clickWrapper(dismiss) { onBlockChange(false) },
        iconId = KonnektIcon.eye
    )
    CommonDropdownItems(
        archived = archived,
        dismiss = dismiss,
        onArchive = onArchive,
        onClearChat = onClearChat
    )
    if (!blocked) DropdownItem(
        text = "Block",
        onClick = clickWrapper(dismiss) { onBlockChange(true) },
        contentColor = destructiveActionColor,
        iconId = KonnektIcon.circleOff
    )
}

@Composable
internal fun ColumnScope.GroupDropdownItems(
    archived: Boolean,
    dismiss: () -> Unit,
    onArchive: () -> Unit,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit
) {
    CommonDropdownItems(
        archived = archived,
        dismiss = dismiss,
        onArchive = onArchive,
        onClearChat = onClearChat
    )
    DropdownItem(
        text = "Leave",
        onClick = clickWrapper(dismiss, onLeaveChat),
        contentColor = Red,
        iconId = KonnektIcon.logOut
    )
}

@Composable
private fun ColumnScope.CommonDropdownItems(
    archived: Boolean,
    dismiss: () -> Unit,
    onArchive: () -> Unit,
    onClearChat: () -> Unit
) {
    if (!archived) DropdownItem(
        text = "Archive",
        onClick = clickWrapper(dismiss, onArchive),
        iconId = KonnektIcon.archive
    )
    DropdownItem(
        text = "Clear Chat",
        onClick = clickWrapper(dismiss, onClearChat),
        contentColor = destructiveActionColor,
        iconId = KonnektIcon.messageCircleX
    )
}