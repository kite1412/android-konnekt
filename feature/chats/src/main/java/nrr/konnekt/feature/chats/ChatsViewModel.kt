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
import kotlinx.coroutines.flow.stateIn
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.usecase.ObserveChatMessagesUseCase
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.feature.chats.util.ChatFilter
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    authentication: Authentication,
    observeChatMessagesUseCase: ObserveChatMessagesUseCase
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
            ChatFilter.PERSON -> filterBySearch.filter { it.chat.type == ChatType.PERSONAL }
            ChatFilter.GROUP -> filterBySearch.filter { it.chat.type == ChatType.GROUP }
            ChatFilter.CHAT_ROOM -> filterBySearch.filter { it.chat.type == ChatType.CHAT_ROOM }
        }
    }
    internal val currentUser = authentication.loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}