package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Chat(
    val id: String,
    val type: ChatType,
    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,
    val setting: ChatSetting?,
    val participants: List<ChatParticipant> = emptyList()
)
