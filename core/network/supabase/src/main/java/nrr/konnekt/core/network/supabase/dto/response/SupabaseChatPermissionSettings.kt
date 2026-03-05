package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.ChatPermissionSettings

@Serializable
internal data class SupabaseChatPermissionSettings(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("edit_chat_info")
    val editChatInfo: Boolean,
    @SerialName("send_messages")
    val sendMessages: Boolean,
    @SerialName("manage_members")
    val manageMembers: Boolean
)

internal fun SupabaseChatPermissionSettings.toModel() =
    ChatPermissionSettings(
        editChatInfo = editChatInfo,
        sendMessages = sendMessages,
        manageMembers = manageMembers
    )