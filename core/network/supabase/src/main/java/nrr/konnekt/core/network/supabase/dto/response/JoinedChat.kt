package nrr.konnekt.core.network.supabase.dto.response

import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JoinedChat(
    @SerialName("user_id")
    val userId: String,
    @SerialName("chat_id")
    val chatId: String
)

internal fun joinedChatColumns() = Columns.list("user_id", "chat_id")