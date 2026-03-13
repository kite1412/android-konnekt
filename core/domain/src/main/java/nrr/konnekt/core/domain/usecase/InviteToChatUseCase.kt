package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.ChatRepository
import javax.inject.Inject

class InviteToChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatId: String,
        receiverIds: List<String>
    ) = chatRepository.inviteToChat(chatId, receiverIds)
}