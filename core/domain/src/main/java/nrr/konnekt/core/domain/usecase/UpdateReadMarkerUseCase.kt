package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject

class UpdateReadMarkerUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(chatId: String) =
        messageRepository.updateUserReadMarker(chatId)
}