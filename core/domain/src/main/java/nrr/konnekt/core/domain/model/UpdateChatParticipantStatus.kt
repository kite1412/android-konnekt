package nrr.konnekt.core.domain.model

data class UpdateChatParticipantStatus(
    val chatId: String,
    val updateClearedAt: Boolean = false,
    val updateLeftAt: Boolean = false,
    val updateArchivedAt: Boolean = false,
    val updateLastReadAt: Boolean = false
)
