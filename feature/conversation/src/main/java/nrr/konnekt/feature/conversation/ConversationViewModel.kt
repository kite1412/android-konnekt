package nrr.konnekt.feature.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import javax.inject.Inject

class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatId: String = checkNotNull(
        savedStateHandle.toRoute<ConversationRoute>().chatId
    )
}