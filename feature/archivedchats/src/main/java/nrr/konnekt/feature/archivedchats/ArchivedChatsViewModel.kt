package nrr.konnekt.feature.archivedchats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.usecase.ObserveChatMessagesUseCase
import javax.inject.Inject

@HiltViewModel
class ArchivedChatsViewModel @Inject constructor(
    authentication: Authentication,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase
) : ViewModel() {
    internal val currentUser = authentication.loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _archivedChats = MutableStateFlow<List<LatestChatMessage>?>(null)
    internal val archivedChats = _archivedChats.asStateFlow()

    init {
        viewModelScope.launch {
            observeChatMessagesUseCase()
                .onEach { latestChatMessages ->
                    _archivedChats.value = latestChatMessages
                        .filter { latestChatMessage ->
                            latestChatMessage.chat.participants
                                .firstOrNull { participant ->
                                    participant.user.id == currentUser.value?.id
                                }
                                ?.status
                                ?.archivedAt != null
                        }
                }
                .launchIn(viewModelScope)
        }
    }
}