package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.ChatRepository
import javax.inject.Inject

class DismissChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatId: String
    ) = chatRepository.dismissChatRoom(chatId)
}