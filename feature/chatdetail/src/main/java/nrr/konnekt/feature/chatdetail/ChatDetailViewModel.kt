package nrr.konnekt.feature.chatdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import nrr.konnekt.feature.chatdetail.navigation.ChatDetailRoute
import nrr.konnekt.feature.chatdetail.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val chatId = savedStateHandle.toRoute<ChatDetailRoute>().chatId
    private val peerId = savedStateHandle.toRoute<ChatDetailRoute>().peerId

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            if (!(chatId != null).xor(peerId != null)) {
                _events.emit(UiEvent.NavigateBack)
                return@launch
            }
        }
    }
}