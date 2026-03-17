package nrr.konnekt.feature.chats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.ChatSettingEdit
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.model.UpdateChatParticipantStatus
import nrr.konnekt.core.domain.model.UpdateStatus
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.usecase.CreateChatUseCase
import nrr.konnekt.core.domain.usecase.FindUsersByUsernameUseCase
import nrr.konnekt.core.domain.usecase.ObserveChatMessagesUseCase
import nrr.konnekt.core.domain.usecase.UpdateChatParticipantStatusUseCase
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.util.UiEvent
import nrr.konnekt.feature.chats.util.ChatFilter
import nrr.konnekt.feature.chats.util.CreateGroupChatSetting
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    authentication: Authentication,
    observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val chatRepository: ChatRepository,
    private val findUsersByUsernameUseCase: FindUsersByUsernameUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val updateChatParticipantStatusUseCase: UpdateChatParticipantStatusUseCase
) : ViewModel() {
    internal var chatFilter by mutableStateOf(ChatFilter.ALL)
    internal var searchValue by mutableStateOf("")

    private var _chats = emptyFlow<List<LatestChatMessage>?>()
    internal var chats = emptyFlow<List<LatestChatMessage>?>()
        private set
    internal var currentUser = emptyFlow<User?>()
        private set
    internal var contacts by mutableStateOf<List<User>?>(null)
        private set
    internal var createChatType by mutableStateOf<ChatType?>(null)
    internal var usersByIdentifier by mutableStateOf<List<User>?>(null)
    internal var chatInvitations by mutableStateOf<List<ChatInvitation>>(emptyList())
        private set
    internal var createChatActionEnabled by mutableStateOf(true)
    internal var createGroupChatSetting by mutableStateOf(CreateGroupChatSetting())

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            currentUser = authentication.loggedInUser
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null
                )
            _chats = observeChatMessagesUseCase()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null
                )
            chats = combine(
                flow = _chats,
                flow2 = snapshotFlow { chatFilter },
                flow3 = snapshotFlow { searchValue }
            ) { chats, filter, searchValue ->
                if (chats == null) return@combine null

                val chats = chats
                    .sortedBy { it.chat.type != ChatType.CHAT_ROOM }
                val filterBySearch = if (searchValue.isNotBlank()) chats.filter {
                    it.chat.setting
                        ?.name?.contains(
                            other = searchValue,
                            ignoreCase = true
                        ) == true
                } else chats
                when (filter) {
                    ChatFilter.ALL -> filterBySearch
                    ChatFilter.PERSONAL -> filterBySearch.filter { it.chat.type == ChatType.PERSONAL }
                    ChatFilter.GROUP -> filterBySearch.filter { it.chat.type == ChatType.GROUP }
                    ChatFilter.CHAT_ROOM -> filterBySearch.filter { it.chat.type == ChatType.CHAT_ROOM }
                }
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null
                )

            chatRepository.observeCurrentUserChatInvitations()
                .onEach { invitations ->
                    _chats.first { chats -> chats != null }
                        ?.let { chats ->
                            chatInvitations = invitations.filter { invitation ->
                                val currentUser = currentUser.first()

                                invitation.chat.id !in chats
                                    .filter { chatMessage ->
                                        chatMessage.chat.participants
                                            .firstOrNull { participant ->
                                                participant.user.id == currentUser?.id
                                            }
                                            ?.status
                                            ?.leftAt == null
                                    }
                                    .map { it.chat.id } &&
                                        invitation.acceptedAt == null &&
                                        invitation.canceledAt == null
                            }
                        }
                }
                .launchIn(viewModelScope)

            currentUser.first { it != null }?.id?.let { userId ->
                chatRepository.getJoinedChats(
                    userId = userId,
                    type = ChatType.PERSONAL
                )
                    .let { res ->
                        if (res is Result.Success) {
                            contacts = res.data
                                .filter { chat ->
                                    chat.type == ChatType.PERSONAL &&
                                            chat.participants
                                                .firstOrNull { participant ->
                                                    participant.user.id == userId
                                                }
                                                ?.status
                                                ?.leftAt == null
                                }
                                .mapNotNull { chatMessage ->
                                    chatMessage.participants
                                        .firstOrNull { participant ->
                                            participant.user.id != userId
                                        }
                                        ?.user
                                }
                        }
                    }
            }
        }
    }

    internal fun findUsers(username: String) {
        viewModelScope.launch {
            usersByIdentifier = null
            with(findUsersByUsernameUseCase(username)) {
                usersByIdentifier = if (this is Result.Success) data
                else emptyList()
            }
        }
    }

    internal fun getPersonalChat(
        otherUserId: String,
        complete: (id: String, exists: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            createChatActionEnabled = false
            var exists = true
            _chats.firstOrNull()?.let { chats ->
                chats.firstOrNull { c ->
                    c.chat.type == ChatType.PERSONAL
                            && c.chat.participants.firstOrNull { p ->
                        p.user.id == otherUserId
                    } != null
                }?.chat?.id ?: run {
                    exists = false
                    otherUserId
                }
            }?.let {
                createChatActionEnabled = true
                complete(it, exists)
            }
        }
    }

    internal fun createChatRoom(
        name: String,
        participants: List<User>,
        complete: (String) -> Unit
    ) {
        viewModelScope.launch {
            createChatActionEnabled = false
            val res = createChatUseCase(
                type = ChatType.CHAT_ROOM,
                chatSetting = ChatSettingEdit(
                    name = name,
                    permissionSettings = ChatPermissionSettings(
                        manageMembers = true
                    )
                ),
                participantIds = participants
                    .takeIf { it.isNotEmpty() }
                    ?.map(User::id)
            )
            createChatActionEnabled = true
            if (res is Result.Success) complete(res.data.id)
        }
    }

    internal fun createGroupChat(complete: (String) -> Unit) {
        viewModelScope.launch {
            createChatActionEnabled = false

            val res = createChatUseCase(
                type = ChatType.GROUP,
                chatSetting = ChatSettingEdit(
                    name = createGroupChatSetting.name,
                    icon = createGroupChatSetting.icon
                )
            )

            createGroupChatSetting = CreateGroupChatSetting()
            createChatActionEnabled = true
            if (res is Result.Success) complete(res.data.id)
        }
    }

    internal fun updateUserStatus(
        chat: Chat,
        updateLeftAt: UpdateStatus? = null,
        updateArchivedAt: Boolean = false,
        updateClearedAt: Boolean = false
    ) {
        viewModelScope.launch {
            updateChatParticipantStatusUseCase(
                update = UpdateChatParticipantStatus(
                    chatId = chat.id,
                    updateLeftAt = updateLeftAt,
                    updateArchivedAt = if (updateArchivedAt) UpdateStatus() else null,
                    updateClearedAt = if (updateClearedAt) UpdateStatus() else null
                )
            )
        }
    }

    internal fun acceptChatInvitation(
        invitation: ChatInvitation,
        onComplete: (chatId: String) -> Unit
    ) {
        viewModelScope.launch {
            chatRepository.joinChat(invitation.id)
                .let { res ->
                    if (res is Result.Success) {
                        _events.emit(UiEvent.ShowSnackbar("Joined \"${invitation.chat.name()}\"."))
                        onComplete(invitation.chat.id)
                    }
                }
        }
    }

    internal fun rejectChatInvitation(invitation: ChatInvitation) {
        viewModelScope.launch {
            chatRepository
                .cancelChatInvitations(listOf(invitation.id))
                .let { res ->
                    if (res is Result.Success && res.data) {
                        _events.emit(UiEvent.ShowSnackbar("Chat invitation rejected."))
                    }
                }
        }
    }
}