package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.dto.ChatSettingEdit
import nrr.konnekt.core.domain.repository.ChatRepository
import javax.inject.Inject

class EditChatSettingUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatId: String,
        chatSetting: ChatSettingEdit
    ) = chatRepository.updateChatSetting(
        chatId = chatId,
        chatSetting = chatSetting
    )
}