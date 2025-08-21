package nrr.konnekt.feature.conversation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.MessageRepository
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import nrr.konnekt.feature.conversation.util.UiEvent
import javax.inject.Inject
import kotlin.time.Instant

@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authentication: Authentication,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val userPresenceManager: UserPresenceManager
) : ViewModel() {
    private val chatId: String = checkNotNull(
        savedStateHandle.toRoute<ConversationRoute>().chatId
    )
    internal val currentUser = authentication.loggedInUser
    internal val messages = messageRepository.observeMessages(chatId)
    internal var messageInput by mutableStateOf("")

    private var _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    private val _totalActiveParticipants = MutableStateFlow<Int?>(null)
    internal val totalActiveParticipants = _totalActiveParticipants.asStateFlow()

    private val _peerLastActive = MutableStateFlow<Instant?>(null)
    internal val peerLastActive = _peerLastActive.asStateFlow()

    init {
        viewModelScope.launch {
            val res = chatRepository.getChatById(chatId)
            when (res) {
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
            if (res is Result.Success) when (res.data.type) {
                ChatType.PERSONAL -> {
                    val data = res.data

                    data.participants.firstOrNull { p ->
                        authentication.getLoggedInUserOrNull()?.id?.let {
                            p.userId != it
                        } == true
                    }
                        ?.let {
                            userPresenceManager
                                .observeUserPresence(it.userId)
                                .onEach { np ->
                                     np?.let { p ->
                                         _totalActiveParticipants.value =
                                             if (p.isActive) 1 else 0
                                         _peerLastActive.value = p.status.lastActiveAt
                                     }
                                }
                                .launchIn(viewModelScope)
                        }
                }
                else -> {}
            }
        }
    }
}