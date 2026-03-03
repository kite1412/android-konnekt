package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.model.UpdateChatParticipantStatus
import nrr.konnekt.core.domain.repository.ChatRepository
import javax.inject.Inject

class UpdateChatParticipantStatusUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(update: UpdateChatParticipantStatus) =
        chatRepository.updateCurrentUserChatParticipantStatus(update)
}