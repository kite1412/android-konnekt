package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.model.ChatParticipantStatus
import javax.inject.Inject

class UpdateChatParticipantStatusUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, status: ChatParticipantStatus) =
        chatRepository.updateCurrentUserChatParticipantStatus(
            chatId = chatId,
            status = status
        )
}