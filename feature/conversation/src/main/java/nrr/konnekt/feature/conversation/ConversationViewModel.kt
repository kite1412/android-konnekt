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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.MessageRepository.MessageError
import nrr.konnekt.core.domain.repository.UserRepository
import nrr.konnekt.core.domain.usecase.CreateChatUseCase
import nrr.konnekt.core.domain.usecase.ObserveMessagesUseCase
import nrr.konnekt.core.domain.usecase.ObserveReadMarkersUseCase
import nrr.konnekt.core.domain.usecase.SendMessageUseCase
import nrr.konnekt.core.domain.usecase.UpdateReadMarkerUseCase
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.media.MediaPlayerManager
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.UserReadMarker
import nrr.konnekt.core.model.util.now
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import nrr.konnekt.feature.conversation.util.ComposerAttachment
import nrr.konnekt.feature.conversation.util.IdType
import nrr.konnekt.feature.conversation.util.LOG_TAG
import nrr.konnekt.feature.conversation.util.MessageAction
import nrr.konnekt.feature.conversation.util.MessageComposerAction
import nrr.konnekt.feature.conversation.util.UiEvent
import nrr.konnekt.feature.conversation.util.toFileUpload
import javax.inject.Inject
import kotlin.time.Instant

@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authentication: Authentication,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val observeReadMarkersUseCase: ObserveReadMarkersUseCase,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val userPresenceManager: UserPresenceManager,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateReadMarkerUseCase: UpdateReadMarkerUseCase,
    private val createChatUseCase: CreateChatUseCase
) : ViewModel() {
    private val chatId: String? = savedStateHandle.toRoute<ConversationRoute>().chatId
    internal val peerId: String? = savedStateHandle.toRoute<ConversationRoute>().peerId
    internal var fixedChatId: String? by mutableStateOf(null)
    internal val currentUser = authentication
        .loggedInUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    internal var messages = emptyFlow<List<Message>>()
    internal var readMarkers = emptyFlow<List<UserReadMarker>>()
    internal var messageInput by mutableStateOf("")
    internal var sendingMessage by mutableStateOf(false)
    internal var composerAction by mutableStateOf<MessageComposerAction?>(null)
    internal val composerAttachments = mutableStateListOf<ComposerAttachment>()
    internal var idType = IdType.CHAT
        private set

    private var _chat = MutableStateFlow<Chat?>(null)
    internal val chat = _chat.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    internal val events = _events.asSharedFlow()

    private val _totalActiveParticipants = MutableStateFlow<Int?>(null)
    internal val totalActiveParticipants = _totalActiveParticipants.asStateFlow()

    private val _peerLastActive = MutableStateFlow<Instant?>(null)
    internal val peerLastActive = _peerLastActive.asStateFlow()

    private val _messageAction = MutableStateFlow<MessageAction?>(null)
    internal val messageAction = _messageAction.asStateFlow()

    init {
        viewModelScope.launch scope@{
            if (!(chatId != null).xor(peerId != null)) {
                _events.emit(UiEvent.NavigateBack)
                return@scope
            }
            if (peerId != null) idType = IdType.USER
            var peerId = peerId
            if (chatId != null) {
                fixedChatId = chatId
                val res = chatRepository.getChatById(chatId)

                when (res) {
                    is Result.Success -> {
                        _chat.value = res.data
                        observeFlows(res.data.id)
                        if (res.data.type == ChatType.PERSONAL) {
                            peerId = res.data.participants.firstOrNull { p ->
                                currentUser.first()?.id?.let {
                                    p.userId != it
                                } == true
                            }?.userId
                        }
                    }
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
            } else if (peerId != null) {
                val res = userRepository.getUserById(peerId)

                if (res is Result.Success) res.data.let {
                    _chat.value = Chat(
                        id = "",
                        type = ChatType.PERSONAL,
                        createdAt = now(),
                        setting = ChatSetting(
                            name = it.username,
                            iconPath = it.imagePath
                        )
                    )
                }
            }
            if (
                _chat.value != null &&
                _chat.value?.type == ChatType.PERSONAL &&
                peerId != null
            ) {
                userPresenceManager
                    .observeUserPresence(peerId)
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
    }

    private fun observeFlows(chatId: String) {
        messages = observeMessagesUseCase(chatId)
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
        readMarkers = observeReadMarkersUseCase(chatId)
            .onEach {
                Log.d(LOG_TAG, "read markers: $it")
            }
    }

    internal fun sendMessage(
        content: String
    ) {
        fixedChatId?.let {
            viewModelScope.launch {
                sendingMessage = true
                val res = sendMessageUseCase(
                    chatId = it,
                    content = content,
                    attachment = composerAttachments
                        .takeIf { a -> a.isNotEmpty() }
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
        } ?: peerId?.let { peerId ->
            _chat.value?.let { chat ->
                viewModelScope.launch {
                    createChatUseCase(
                        type = chat.type,
                        participantIds = listOf(peerId)
                    )
                        .let { r ->
                            if (r is Result.Success) {
                                fixedChatId = r.data.id
                                _chat.update {
                                    it?.copy(
                                        id = r.data.id,
                                        createdAt = r.data.createdAt
                                    )
                                }
                                fixedChatId?.let(::observeFlows)
                                sendMessage(content)
                            }
                        }
                }
            }
        }
    }

    internal fun stopPlayingMedia() = MediaPlayerManager.clearPlayback()

    internal fun setMessageAction(messageAction: MessageAction) {
        _messageAction.value = messageAction
    }

    internal fun dismissMessageAction() {
        _messageAction.value = null
    }
}