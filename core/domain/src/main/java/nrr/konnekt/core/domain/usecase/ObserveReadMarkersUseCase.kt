package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject

class ObserveReadMarkersUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(chatId: String) =
        messageRepository.observeUserReadMarkers(chatId)

    fun currentUser() = messageRepository.observeCurrentUserReadMarkers()
}