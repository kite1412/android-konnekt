package nrr.konnekt.feature.chats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.usecase.ObserveChatMessagesUseCase
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    authentication: Authentication,
    observeChatMessagesUseCase: ObserveChatMessagesUseCase
) : ViewModel() {
    internal val chats = observeChatMessagesUseCase()
    internal val currentUser = authentication.loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
    internal var searchValue by mutableStateOf("")
}