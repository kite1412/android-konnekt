package nrr.konnekt.core.model

import kotlin.time.Instant

data class Chat(
    val id: String,
    val type: ChatType,
    val createdAt: Instant,
    val setting: ChatSetting?
)
