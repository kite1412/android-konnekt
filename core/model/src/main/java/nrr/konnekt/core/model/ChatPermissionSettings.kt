package nrr.konnekt.core.model

data class ChatPermissionSettings(
    val editChatInfo: Boolean = false,
    val sendMessages: Boolean = true,
    val createEvents: Boolean = false,
    val manageMembers: Boolean = false
)