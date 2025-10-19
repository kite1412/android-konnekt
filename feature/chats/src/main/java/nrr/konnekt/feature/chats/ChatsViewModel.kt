package nrr.konnekt.feature.chats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.repository.UserRepository
import nrr.konnekt.core.domain.usecase.CreateChatUseCase
import nrr.konnekt.core.domain.usecase.FindUsersByUsernameUseCase
import nrr.konnekt.core.domain.usecase.ObserveChatMessagesUseCase
import nrr.konnekt.core.domain.usecase.ObserveReadMarkersUseCase
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.feature.chats.util.ChatFilter
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    authentication: Authentication,
    observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    observeReadMarkersUseCase: ObserveReadMarkersUseCase,
    private val userRepository: UserRepository,
    private val findUsersByUsernameUseCase: FindUsersByUsernameUseCase,
    private val createChatUseCase: CreateChatUseCase
) : ViewModel() {
    internal var chatFilter by mutableStateOf(ChatFilter.ALL)
    internal var searchValue by mutableStateOf("")
    private val _chats = observeChatMessagesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    internal val chats = combine(
        flow = _chats,
        flow2 = snapshotFlow { chatFilter },
        flow3 = snapshotFlow { searchValue }
    ) { chats, filter, searchValue ->
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
    internal val myReadMarkers = observeReadMarkersUseCase.currentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    internal val currentUser = authentication.loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
    internal var createChatType by mutableStateOf<ChatType?>(null)
    internal var usersByIdentifier by mutableStateOf<List<User>?>(null)
    internal var createChatActionEnabled by mutableStateOf(true)

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
                        p.userId == otherUserId
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
        complete: (Chat) -> Unit
    ) {
        viewModelScope.launch {
            createChatActionEnabled = false
            val res = createChatUseCase(
                type = ChatType.CHAT_ROOM,
                chatSetting = CreateChatSetting(name)
            )
            createChatActionEnabled = true
            if (res is Result.Success) complete(res.data)
        }
    }
}