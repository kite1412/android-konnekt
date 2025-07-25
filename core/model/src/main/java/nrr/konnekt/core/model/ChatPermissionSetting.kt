package nrr.konnekt.core.model

data class ChatPermissionSetting(
    val editChatInfo: Boolean,
    val sendMessages: Boolean,
    val createEvents: Boolean,
    val manageMembers: Boolean
)