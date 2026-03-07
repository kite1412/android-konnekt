package nrr.konnekt.core.ui.util

import androidx.compose.runtime.Composable
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.ui.component.DropdownItem

object ChatDropdownItems {
    private val clickWrapper = {
            dismiss: () -> Unit,
            action: () -> Unit
        ->
        {
            dismiss()
            action()
        }
    }
    private val destructiveActionColor = Red

    @Composable
    fun Archive(
        dismiss: () -> Unit,
        onArchive: () -> Unit
    ) {
        DropdownItem(
            text = "Archive",
            onClick = clickWrapper(dismiss, onArchive),
            iconId = KonnektIcon.archive
        )
    }

    @Composable
    fun Unarchive(
        dismiss: () -> Unit,
        onUnarchive: () -> Unit
    ) {
        DropdownItem(
            text = "Unarchive",
            onClick = clickWrapper(dismiss, onUnarchive),
            iconId = KonnektIcon.archiveRestore,
        )
    }

    @Composable
    fun ClearChat(
        dismiss: () -> Unit,
        onClearChat: () -> Unit
    ) {
        DropdownItem(
            text = "Clear Chat",
            onClick = clickWrapper(dismiss, onClearChat),
            contentColor = destructiveActionColor,
            iconId = KonnektIcon.messageCircleX
        )
    }

    @Composable
    fun Block(
        dismiss: () -> Unit,
        onBlockChange: (blocked: Boolean) -> Unit
    ) {
        DropdownItem(
            text = "Block",
            onClick = clickWrapper(dismiss) { onBlockChange(true) },
            contentColor = destructiveActionColor,
            iconId = KonnektIcon.circleOff
        )
    }

    @Composable
    fun Unblock(
        dismiss: () -> Unit,
        onBlockChange: (blocked: Boolean) -> Unit
    ) {
        DropdownItem(
            text = "Unblock",
            onClick = clickWrapper(dismiss) { onBlockChange(false) },
            iconId = KonnektIcon.eye
        )
    }

    @Composable
    fun Leave(
        dismiss: () -> Unit,
        onLeaveChat: () -> Unit
    ) {
        DropdownItem(
            text = "Leave",
            onClick = clickWrapper(dismiss, onLeaveChat),
            contentColor = Red,
            iconId = KonnektIcon.logOut
        )
    }
}