package nrr.konnekt.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatPermissionSetting(
    @SerialName("edit_chat_info")
    val editChatInfo: Boolean,
    @SerialName("send_messages")
    val sendMessages: Boolean,
    @SerialName("create_events")
    val createEvents: Boolean,
    @SerialName("manage_members")
    val manageMembers: Boolean
)