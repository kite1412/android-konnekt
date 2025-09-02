package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class UserReadMarker(
    @SerialName("user_id")
    val userId: String,
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("last_read_at")
    @Contextual
    val lastReadAt: Instant? = null
)
