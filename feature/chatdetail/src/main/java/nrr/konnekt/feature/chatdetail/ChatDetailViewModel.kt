package nrr.konnekt.feature.chatdetail

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
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.feature.chatdetail.navigation.ChatDetailRoute
import nrr.konnekt.feature.chatdetail.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val chatId = savedStateHandle.toRoute<ChatDetailRoute>().chatId
    private val peerId = savedStateHandle.toRoute<ChatDetailRoute>().peerId

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    private val _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    private val _totalChatParticipants = MutableStateFlow<Int?>(null)
    internal val totalChatParticipants = _totalChatParticipants.asStateFlow()

    init {
        viewModelScope.launch {
            if (!(chatId != null).xor(peerId != null)) {
                _events.emit(UiEvent.NavigateBack)
                return@launch
            }
            if (chatId != null) {
                chatRepository.getChatById(chatId).let {
                    when (it) {
                        is Result.Success -> _chat.value = it.data
                        is Result.Error -> _events.emit(UiEvent.NavigateBack)
                    }
                }
            }
        }
    }
}