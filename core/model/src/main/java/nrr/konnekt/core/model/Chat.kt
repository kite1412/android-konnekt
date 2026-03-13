package nrr.konnekt.core.model

import kotlin.time.Instant

data class Chat(
    val id: String,
    val type: ChatType,
    val createdAt: Instant,
    val deletedAt: Instant? = null,
    val setting: ChatSetting?,
    val participants: List<ChatParticipant> = emptyList()
)
