package nrr.konnekt.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.repository.ChatRepository
import javax.inject.Inject

class ObserveChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<LatestChatMessage>> =
        chatRepository.observeLatestChatMessages()
}