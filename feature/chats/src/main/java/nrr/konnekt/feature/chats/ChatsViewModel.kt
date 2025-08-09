package nrr.konnekt.feature.chats

import dagger.hilt.android.lifecycle.HiltViewModel
import nrr.konnekt.core.domain.usecase.ObserveChatMessagesUseCase
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase
) {
    internal val chats = observeChatMessagesUseCase()
}