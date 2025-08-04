package nrr.konnekt.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatPermissionSettings(
    @SerialName("edit_chat_info")
    val editChatInfo: Boolean = false,
    @SerialName("send_messages")
    val sendMessages: Boolean = true,
    @SerialName("create_events")
    val createEvents: Boolean = false,
    @SerialName("manage_members")
    val manageMembers: Boolean = false
)