package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        chatId: String,
        content: String,
        attachment: List<FileUpload>? = null
    ) = messageRepository.sendMessage(
        chatId = chatId,
        content = content,
        attachments = attachment
    )
}