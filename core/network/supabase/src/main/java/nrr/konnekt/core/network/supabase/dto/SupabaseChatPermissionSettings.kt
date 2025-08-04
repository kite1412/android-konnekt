package nrr.konnekt.core.network.supabase.dto

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
    @SerialName("create_events")
    val createEvents: Boolean,
    @SerialName("manage_members")
    val manageMembers: Boolean
)

internal fun ChatPermissionSettings.toSupabaseChatPermissionSettings(
    chatId: String
) = SupabaseChatPermissionSettings(
    chatId = chatId,
    editChatInfo = editChatInfo,
    sendMessages = sendMessages,
    createEvents = createEvents,
    manageMembers = manageMembers
)