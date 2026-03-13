package nrr.konnekt.feature.chatdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.model.UpdateChatParticipantStatus
import nrr.konnekt.core.domain.model.UpdateStatus
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.usecase.ObserveChatParticipantsUseCase
import nrr.konnekt.core.domain.usecase.UpdateChatParticipantStatusUseCase
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.util.UiEvent
import nrr.konnekt.feature.chatdetail.navigation.ChatDetailRoute
import javax.inject.Inject
import kotlin.time.Instant

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authentication: Authentication,
    private val chatRepository: ChatRepository,
    private val userPresenceManager: UserPresenceManager,
    private val updateChatParticipantStatusUseCase: UpdateChatParticipantStatusUseCase,
    private val observeChatParticipantsUseCase: ObserveChatParticipantsUseCase
) : ViewModel() {
    private val chatId = savedStateHandle.toRoute<ChatDetailRoute>().chatId
    private val peerId = savedStateHandle.toRoute<ChatDetailRoute>().peerId

    internal val currentUser = authentication
        .loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    internal val isPersonalChatAdded = peerId == null
    internal var peerGroupsInCommon = mutableStateListOf<Chat>()
    internal var chatInvitations = mutableStateListOf<ChatInvitation>()
    internal var peerLastActiveAt by mutableStateOf<Instant?>(null)
    internal var currentUserContacts: List<User>? by mutableStateOf(null)

    private val _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    private val _activeParticipants = MutableStateFlow<List<ChatParticipant>>(emptyList())
    internal val activeParticipants = _activeParticipants.asStateFlow()

    init {
        viewModelScope.launch {
            if (!(chatId != null).xor(peerId != null)) {
                _events.emit(UiEvent.NavigateBack)
                return@launch
            }
            if (chatId != null) {
                chatRepository.getChatById(chatId).let {
                    when (it) {
                        is Result.Success -> {
                            val chat = it.data
                            _chat.value = chat

                            if (chat.type == ChatType.PERSONAL) {
                                val currentUser = currentUser.first()

                                chat
                                    .participants
                                    .firstOrNull { participant ->
                                        participant.user.id != currentUser?.id
                                    }
                                    ?.let { participant ->
                                        val res = chatRepository.getJoinedChats(participant.user.id)

                                        userPresenceManager
                                            .observeUserPresence(participant.user.id)
                                            .onEach { userPresence ->
                                                peerLastActiveAt = userPresence?.status?.lastActiveAt
                                            }
                                            .launchIn(viewModelScope)

                                        if (res is Result.Success)
                                            peerGroupsInCommon.addAll(
                                                res.data
                                                    .filter { chat ->
                                                        chat.type != ChatType.PERSONAL && chat.participants
                                                            .firstOrNull { participant ->
                                                                currentUser?.id == participant.user.id
                                                            }
                                                            ?.let { participant ->
                                                                participant.status.leftAt == null
                                                            } ?: false
                                                    }
                                            )
                                    }
                            } else {
                                val res = chatRepository.getChatInvitations(chat.id)

                                if (res is Result.Success) {
                                    chatInvitations.addAll(
                                        res.data
                                            .sortedBy { invitation -> invitation.invitedAt }
                                            .distinctBy { invitation -> invitation.id }
                                    )
                                }
                            }
                            observeChatParticipantsUseCase
                                .activeParticipants(chat.id)
                                .onEach { participants ->
                                    _activeParticipants.value = participants
                                        .filter { participant ->
                                            participant.status.leftAt == null
                                        }
                                }
                                .launchIn(viewModelScope)

                            observeChatParticipantsUseCase
                                .currentUser(chat.id)
                                .onEach { currentUserChatParticipation ->
                                    _chat.value?.let { prev ->
                                        _chat.value = prev.copy(
                                            participants = mutableListOf<ChatParticipant>().apply {
                                                addAll(chat.participants)

                                                indexOfFirst { participant ->
                                                    currentUserChatParticipation?.participation?.user?.id == participant.user.id
                                                }
                                                    .let { index ->
                                                        if (index != -1) {
                                                            currentUserChatParticipation?.participation?.let { participation ->
                                                                set(index, participation)
                                                            }
                                                        }
                                                    }
                                            }
                                        )
                                    }
                                }
                                .launchIn(viewModelScope)
                        }
                        is Result.Error -> _events.emit(UiEvent.NavigateBack)
                    }
                }
            }
        }
    }

    internal fun updateChatParticipantStatus(
        updateLeftAt: UpdateStatus? = null,
        updateClearedAt: Boolean = false
    ) {
        viewModelScope.launch {
            chat.value?.let { chat ->
                updateChatParticipantStatusUseCase(
                    update = UpdateChatParticipantStatus(
                        chatId = chat.id,
                        updateLeftAt = updateLeftAt,
                        updateClearedAt = if (updateClearedAt) UpdateStatus() else null
                    )
                )
            }
        }
    }

    internal suspend fun getPersonalChatId(user: User): String? {
        val res = chatRepository.getJoinedChats(user.id)

        return if (res is Result.Success) {
            val currentUserId = currentUser.first()?.id

            res.data
                .firstOrNull { chat ->
                    chat.type == ChatType.PERSONAL &&
                            chat.participants.any { participant ->
                                participant.user.id == currentUserId
                            }
                }
                ?.id
        } else null
    }

    internal fun updateCurrentUserContacts() {
        if (currentUserContacts == null) {
            viewModelScope.launch {
                currentUser.first()?.let { user ->
                    val res = chatRepository.getJoinedChats(
                        userId = user.id,
                        type = ChatType.PERSONAL
                    )

                    if (res is Result.Success) {
                        currentUserContacts = res.data.mapNotNull { chat ->
                            chat.participants
                                .firstOrNull { participant ->
                                    participant.user.id != user.id
                                }
                                ?.user
                        }
                    }
                }
            }
        }
    }

    internal fun cancelInvitations(invitationIds: List<String>) {
        viewModelScope.launch {
            val res = chatRepository.cancelChatInvitations(invitationIds)

            if (res is Result.Success && res.data) {
                chatInvitations.removeAll { invitation ->
                    invitationIds.contains(invitation.id)
                }
                _events.emit(
                    UiEvent.ShowSnackbar("Invitations cancelled")
                )
            }
        }
    }
}