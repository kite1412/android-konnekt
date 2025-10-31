package nrr.konnekt.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Event(
    val id: String,
    @SerialName("created_by")
    val createdBy: String,
    @SerialName("chat_id")
    val chatId: String,
    val title: String,
    val description: String?,
    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,
    @SerialName("edited_at")
    val editedAt: Instant,
    @SerialName("starts_at")
    val startsAt: Instant
)