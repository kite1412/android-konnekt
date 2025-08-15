package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    val role: ParticipantRole,
    @SerialName("joined_at")
    @Contextual
    val joinedAt: Instant,
    @SerialName("left_at")
    @Contextual
    val leftAt: Instant?
)