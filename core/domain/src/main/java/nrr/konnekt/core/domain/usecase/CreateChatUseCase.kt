package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.model.ChatType
import javax.inject.Inject

class CreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        type: ChatType,
        chatSetting: CreateChatSetting? = null,
        participantIds: List<String>? = null
    ) = chatRepository.createChat(
        type = type,
        chatSetting = chatSetting,
        participantIds = participantIds
    )
}