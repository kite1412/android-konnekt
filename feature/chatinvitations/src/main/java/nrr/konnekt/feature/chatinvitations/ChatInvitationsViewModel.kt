package nrr.konnekt.feature.chatinvitations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.ui.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class ChatInvitationsViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    internal var chatInvitations by mutableStateOf<List<ChatInvitation>?>(null)
        private set

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    init {
        chatRepository.observeCurrentUserChatInvitations()
            .onEach { invitations ->
                chatInvitations = invitations.filter { invitation ->
                    invitation.chat.type != ChatType.CHAT_ROOM &&
                            invitation.acceptedAt == null &&
                            invitation.canceledAt == null
                }
            }
            .launchIn(viewModelScope)
    }

    internal fun onInvitationAction(invitation: ChatInvitation, accepted: Boolean) {
        viewModelScope.launch {
            if (accepted) {
                val res = chatRepository.joinChat(invitation.id)

                if (res is Result.Success) {
                    _events.emit(UiEvent.ShowSnackbar("Joined \"${invitation.chat.name()}\"."))
                }
            }
            else {
                chatRepository.cancelChatInvitations(listOf(invitation.id))
                    .takeIf { it is Result.Success && it.data }
                    ?.let {
                        _events.emit(UiEvent.ShowSnackbar("Invitation rejected."))
                    }
            }
        }
    }
}