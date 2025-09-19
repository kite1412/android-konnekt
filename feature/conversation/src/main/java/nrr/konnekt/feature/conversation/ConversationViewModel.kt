package nrr.konnekt.feature.conversation

import android.util.Log
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
import nrr.konnekt.core.domain.FileUploadConstraints
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.MessageRepository.MessageError
import nrr.konnekt.core.domain.usecase.ObserveMessagesUseCase
import nrr.konnekt.core.domain.usecase.ObserveReadMarkersUseCase
import nrr.konnekt.core.domain.usecase.SendMessageUseCase
import nrr.konnekt.core.domain.usecase.UpdateReadMarkerUseCase
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import nrr.konnekt.feature.conversation.util.ComposerAttachment
import nrr.konnekt.feature.conversation.util.LOG_TAG
import nrr.konnekt.feature.conversation.util.MessageComposerAction
import nrr.konnekt.feature.conversation.util.UiEvent
import nrr.konnekt.feature.conversation.util.toFileUpload
import javax.inject.Inject
import kotlin.time.Instant

@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authentication: Authentication,
    observeMessagesUseCase: ObserveMessagesUseCase,
    observeReadMarkersUseCase: ObserveReadMarkersUseCase,
    internal val fileUploadConstraints: FileUploadConstraints,
    private val chatRepository: ChatRepository,
    private val userPresenceManager: UserPresenceManager,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateReadMarkerUseCase: UpdateReadMarkerUseCase
) : ViewModel() {
    private val chatId: String = checkNotNull(
        savedStateHandle.toRoute<ConversationRoute>().chatId
    )
    internal val currentUser = authentication
        .loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    internal val messages = observeMessagesUseCase(chatId)
        .onEach { l ->
            currentUser.first()?.id?.let { id ->
                l.firstOrNull()
                    ?.sender
                    ?.id
                    ?.let {
                        if (id != it) updateReadMarkerUseCase(chatId, l.first().sentAt)
                    }
            }
        }
    internal val readMarkers = observeReadMarkersUseCase(chatId)
        .onEach {
            Log.d(LOG_TAG, "read markers: $it")
        }
    internal var messageInput by mutableStateOf("")
    internal var sendingMessage by mutableStateOf(false)
    internal var composerAction by mutableStateOf<MessageComposerAction?>(null)
    internal val composerAttachments = mutableStateListOf<ComposerAttachment>()

    private var _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    private val _totalActiveParticipants = MutableStateFlow<Int?>(null)
    internal val totalActiveParticipants = _totalActiveParticipants.asStateFlow()

    private val _peerLastActive = MutableStateFlow<Instant?>(null)
    internal val peerLastActive = _peerLastActive.asStateFlow()

    init {
        viewModelScope.launch {
            val res = chatRepository.getChatById(chatId)
            when (res) {
                is Result.Success -> _chat.value = res.data
                is Result.Error -> {
                    _events.emit(
                        UiEvent.ShowSnackbar(
                            when (res) {
                                ChatError.ChatNotFound -> "Chat not found"
                                else -> "Fail to fetch chat data"
                            }
                        )
                    )
                    _events.emit(UiEvent.NavigateBack)
                }
            }
            if (res is Result.Success) when (res.data.type) {
                ChatType.PERSONAL -> {
                    val data = res.data

                    data.participants.firstOrNull { p ->
                        currentUser.first()?.id?.let {
                            p.userId != it
                        } == true
                    }
                        ?.let { p ->
                            userPresenceManager
                                .observeUserPresence(p.userId)
                                .onEach { np ->
                                     np?.let { p ->
                                         _totalActiveParticipants.value =
                                             if (p.isActive) 1 else 0
                                         _peerLastActive.value = p.status.lastActiveAt
                                     }
                                }
                                .launchIn(viewModelScope)
                        }
                }
                else -> {}
            }
        }
    }

    internal fun sendMessage(
        content: String
    ) {
        viewModelScope.launch {
            sendingMessage = true
            val res = sendMessageUseCase(
                chatId = chatId,
                content = content,
                attachment = composerAttachments
                    .takeIf { it.isNotEmpty() }
                    ?.map(ComposerAttachment::toFileUpload)
            )
            composerAttachments.clear()
            if (res is Result.Error) {
                _events.emit(
                    value = UiEvent.ShowSnackbar(
                        when (res.error) {
                            is MessageError.ChatNotFound -> "Chat not found"
                            is MessageError.FileUploadError -> "File upload error"
                            is MessageError.DisallowedFileType -> "Disallowed file type: " +
                                    (res.error as MessageError.DisallowedFileType)
                                        .fileTypes
                                        .joinToString()
                            is MessageError.MessageNotFound -> "Message not found"
                            else -> "Fail to send message"
                        }
                    )
                )
            }
            messageInput = ""
            sendingMessage = false
        }
    }
}