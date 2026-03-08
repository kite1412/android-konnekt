package nrr.konnekt.core.ui.util

import nrr.konnekt.core.ui.component.Alert

fun unarchiveChatAlert(chatName: String, onConfirm: () -> Unit) =
    Alert(
        onConfirm = onConfirm,
        title = "Unarchive Chat",
        message = "Unarchive $chatName?"
    )

fun archiveChatAlert(chatName: String, onConfirm: () -> Unit) =
    Alert(
        onConfirm = onConfirm,
        title = "Archive Chat",
        message = "Archive $chatName?"
    )

fun clearChatAlert(chatName: String, onConfirm: () -> Unit) =
    Alert(
        onConfirm = onConfirm,
        title = "Clear Messages",
        message = "Clear all messages in $chatName?"
    )

fun leaveChatAlert(chatName: String, onConfirm: () -> Unit) =
    Alert(
        onConfirm = onConfirm,
        title = "Leave Chat",
        message = "Leave $chatName?"
    )

fun blockChatAlert(chatName: String, onConfirm: () -> Unit) =
    Alert(
        onConfirm = onConfirm,
        title = "Block Chat",
        message = "Block $chatName?"
    )

fun unblockChatAlert(chatName: String, onConfirm: () -> Unit) =
    Alert(
        onConfirm = onConfirm,
        title = "Unblock Chat",
        message = "Unblock $chatName"
    )
