package nrr.konnekt.core.network.supabase.dto.response

import io.github.jan.supabase.realtime.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
internal data class SupabaseChatInvitation(
    val id: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("inviter_id")
    val inviterId: String,
    @SerialName("receiver_id")
    val receiverId: String,
    @SerialName("invited_at")
    val invitedAt: Instant
) {
    companion object {
        val PrimaryKey = PrimaryKey<SupabaseChatInvitation>(columnName = "id") {
            it.id
        }
    }
}
