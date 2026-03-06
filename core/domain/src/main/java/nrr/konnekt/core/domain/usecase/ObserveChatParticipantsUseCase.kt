package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.ChatRepository
import javax.inject.Inject

class ObserveChatParticipantsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatId: String) =
        chatRepository.observeChatParticipants(chatId)

    fun currentUser() = chatRepository.observeCurrentUserChatParticipations()

    fun currentUser(chatId: String) = chatRepository.observeCurrentUserChatParticipation(chatId)

    fun activeParticipants(chatId: String) = chatRepository.observeActiveParticipants(chatId)
}