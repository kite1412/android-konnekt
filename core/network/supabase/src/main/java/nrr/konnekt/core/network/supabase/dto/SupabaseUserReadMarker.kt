package nrr.konnekt.core.network.supabase.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserReadMarker
import kotlin.time.Instant

@Serializable
internal data class SupabaseUserReadMarker(
    @SerialName("user_id")
    val userId: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("last_read_at")
    @Contextual
    val lastReadAt: Instant
)

internal fun SupabaseUserReadMarker.toUserReadMarker(user: User) =
    UserReadMarker(
        user = user,
        chatId = chatId,
        lastReadAt = lastReadAt
    )