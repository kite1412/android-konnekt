package nrr.konnekt.core.domain.usecase

import nrr.konnekt.core.domain.repository.MessageRepository
import javax.inject.Inject
import kotlin.time.Instant

class UpdateReadMarkerUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(chatId: String, instant: Instant? = null) =
        messageRepository.updateUserReadMarker(chatId, instant)
}