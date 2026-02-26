package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject

class EditMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        messageId: String,
        newContent: String
    ) = messageRepository.editMessage(
        messageId = messageId,
        newContent = newContent
    )
}