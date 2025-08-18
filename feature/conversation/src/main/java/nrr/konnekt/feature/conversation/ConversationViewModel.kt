package nrr.konnekt.feature.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.MessageRepository
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import nrr.konnekt.feature.conversation.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authentication: Authentication,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val chatId: String = checkNotNull(
        savedStateHandle.toRoute<ConversationRoute>().chatId
    )
    internal val currentUser = authentication.loggedInUser
    internal val messages = messageRepository.observeMessages(chatId)

    private var _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()


    init {
        viewModelScope.launch {
            when (val res = chatRepository.getChatById(chatId)) {
                is Result.Success -> _chat.value = res.data
                is Result.Error -> {
                    _events.emit(
                        UiEvent.ShowSnackbar(
                            when (res) {
                                ChatError.ChatNotFound -> "Chat not found"
                                else -> "Fail to fetch chat data"
                            }
                        )
                    )
                    _events.emit(UiEvent.NavigateBack)
                }
            }
        }
    }
}