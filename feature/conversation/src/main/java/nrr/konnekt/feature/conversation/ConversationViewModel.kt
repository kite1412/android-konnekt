package nrr.konnekt.feature.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.model.Chat
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val chatId: String = checkNotNull(
        savedStateHandle.toRoute<ConversationRoute>().chatId
    )
    private var _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    init {

    }
}