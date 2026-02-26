package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject

class HideMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
){
    suspend operator fun invoke(messageIds: List<String>) =
        messageRepository.hideMessages(messageIds)
}