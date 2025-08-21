package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(chatId: String) =
        messageRepository.observeMessages(chatId)
}