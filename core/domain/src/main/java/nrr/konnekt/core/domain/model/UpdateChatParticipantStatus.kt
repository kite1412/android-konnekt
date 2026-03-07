package nrr.konnekt.core.domain.model

data class UpdateChatParticipantStatus(
    val chatId: String,
    val updateClearedAt: UpdateStatus? = null,
    val updateLeftAt: UpdateStatus? = null,
    val updateArchivedAt: UpdateStatus? = null,
    val updateLastReadAt: UpdateStatus? = null
)

data class UpdateStatus(
    val reset: Boolean = false
)

/*
    true = update
    false = reset
    null = do nothing
*/
fun UpdateStatus?.updateOrReset() =
    takeIf { this != null }
        ?.reset?.not()