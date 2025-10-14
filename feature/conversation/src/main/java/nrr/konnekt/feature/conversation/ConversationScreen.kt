package nrr.konnekt.feature.conversation

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Lime
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.domain.FileUploadConstraints
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserReadMarker
import nrr.konnekt.core.model.util.FileType
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.model.util.toDateAndTimeString
import nrr.konnekt.core.player.MediaPlayerManager
import nrr.konnekt.core.player.PlaybackState
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.component.DropdownMenu
import nrr.konnekt.core.ui.component.MessageBubble
import nrr.konnekt.core.ui.component.MessageSeenIndicator
import nrr.konnekt.core.ui.component.ProgressBar
import nrr.konnekt.core.ui.compositionlocal.LocalNavigationBarColorManager
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.compositionlocal.LocalStatusBarColorManager
import nrr.konnekt.core.ui.previewparameter.Conversation
import nrr.konnekt.core.ui.previewparameter.ConversationProvider
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.getAudioDurationMs
import nrr.konnekt.core.ui.util.msToString
import nrr.konnekt.core.ui.util.rememberResolvedFile
import nrr.konnekt.core.ui.util.topRadialGradient
import nrr.konnekt.feature.conversation.exception.UriConversionException
import nrr.konnekt.feature.conversation.util.ActionType
import nrr.konnekt.feature.conversation.util.ActiveStatus
import nrr.konnekt.feature.conversation.util.ComposerAttachment
import nrr.konnekt.feature.conversation.util.ConversationItem
import nrr.konnekt.feature.conversation.util.LOG_TAG
import nrr.konnekt.feature.conversation.util.MessageAction
import nrr.konnekt.feature.conversation.util.MessageComposerAction
import nrr.konnekt.feature.conversation.util.UiEvent
import nrr.konnekt.feature.conversation.util.attachments
import nrr.konnekt.feature.conversation.util.dateHeaderString
import nrr.konnekt.feature.conversation.util.mapToConversationItem
import nrr.konnekt.feature.conversation.util.uriToComposerAttachment
import kotlin.time.Instant

@Composable
internal fun ConversationScreen(
    navigateBack: () -> Unit,
    navigateToChatDetail: (Chat) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(null)
    val chat by viewModel.chat.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle(null)
    val readMarkers by viewModel.readMarkers.collectAsStateWithLifecycle(null)
    val totalActiveParticipants by viewModel.totalActiveParticipants.collectAsStateWithLifecycle()
    val peerLastActive by viewModel.peerLastActive.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val messageInput = viewModel.messageInput
    val messageAction by viewModel.messageAction.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY)
                viewModel.stopPlayingMedia()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
                is UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    currentUser?.let { u ->
        chat?.let { c ->
            messages?.let { m ->
                ConversationScreen(
                    currentUser = u,
                    chat = c,
                    messages = m,
                    readMarkers = readMarkers,
                    messageInput = messageInput,
                    messageAction = messageAction,
                    onMessageInputChange = { viewModel.messageInput = it },
                    composerAttachments = viewModel.composerAttachments,
                    onAddComposerAttachment = {
                        viewModel.composerAttachments.add(it)
                    },
                    composerAction = viewModel.composerAction,
                    onComposerActionChange = {
                        viewModel.composerAction = it
                    },
                    onSend = {
                        viewModel.sendMessage(it)
                    },
                    sendingMessage = viewModel.sendingMessage,
                    totalActiveParticipants = totalActiveParticipants ?: 0,
                    onNavigateBack = navigateBack,
                    onChatClick = navigateToChatDetail,
                    onMessageAction = viewModel::setMessageAction,
                    onDismissMessageAction = viewModel::dismissMessageAction,
                    fileUploadConstraints = viewModel.fileUploadConstraints,
                    contentPadding = contentPadding,
                    modifier = modifier,
                    peerLastActive = peerLastActive
                )
            }
        }
    }
}

@Composable
private fun ConversationScreen(
    currentUser: User,
    chat: Chat,
    totalActiveParticipants: Int,
    messages: List<Message>,
    readMarkers: List<UserReadMarker>?,
    messageInput: String,
    messageAction: MessageAction?,
    onMessageInputChange: (String) -> Unit,
    composerAttachments: List<ComposerAttachment>,
    onAddComposerAttachment: (ComposerAttachment) -> Unit,
    composerAction: MessageComposerAction?,
    onComposerActionChange: (MessageComposerAction?) -> Unit,
    onSend: (String) -> Unit,
    sendingMessage: Boolean,
    onNavigateBack: () -> Unit,
    onChatClick: (Chat) -> Unit,
    onMessageAction: (MessageAction) -> Unit,
    onDismissMessageAction: () -> Unit,
    fileUploadConstraints: FileUploadConstraints,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    peerLastActive: Instant? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .bottomRadialGradient()
            .topRadialGradient()
            .padding(contentPadding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Header(
            chat = chat,
            totalActiveParticipants = totalActiveParticipants,
            onNavigateBack = onNavigateBack,
            onChatClick = onChatClick,
            peerLastActive = peerLastActive
        ) {

        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            var showFloatingDateHeader by remember {
                mutableStateOf(true)
            }
            var lastVisibleDate by remember {
                mutableStateOf("")
            }

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val conversationItems = remember(messages) {
                    messages.mapToConversationItem()
                }
                val state = rememberLazyListState()
                val scrollInProgress by remember {
                    derivedStateOf(
                        state::isScrollInProgress
                    )
                }
                val visibleItems by remember {
                    derivedStateOf {
                        state.layoutInfo.visibleItemsInfo
                    }
                }

                LaunchedEffect(scrollInProgress) {
                    if (scrollInProgress) {
                        showFloatingDateHeader = true
                    } else {
                        delay(1000)
                        showFloatingDateHeader = false
                    }
                }
                LaunchedEffect(visibleItems) {
                    if (visibleItems.isNotEmpty()) {
                        visibleItems.lastOrNull()?.index?.let { i ->
                            (conversationItems
                                .slice(i until conversationItems.size)
                                .firstOrNull { item ->
                                    item is ConversationItem.DateHeader
                                } as? ConversationItem.DateHeader)
                                ?.let {
                                    lastVisibleDate = it.date.dateHeaderString()
                                }
                        }
                    }
                }
                Conversation(
                    items = conversationItems,
                    readMarkers = readMarkers,
                    chatType = chat.type,
                    sentByCurrentUser = { m -> m.sender.id == currentUser.id },
                    deletedByCurrentUser = { m ->
                        m.messageStatuses
                            .firstOrNull { it.userId == currentUser.id }
                            ?.isDeleted == true
                    },
                    onMessageAction = onMessageAction,
                    modifier = Modifier.weight(1f),
                    state = state
                )
                MessageComposer(
                    message = messageInput,
                    onMessageChange = onMessageInputChange,
                    attachments = composerAttachments,
                    onAddAttachment = onAddComposerAttachment,
                    action = composerAction,
                    onActionChange = onComposerActionChange,
                    onSend = onSend,
                    sendingMessage = sendingMessage,
                    fileUploadConstraints = fileUploadConstraints
                )
            }
            if (messages.isNotEmpty() && lastVisibleDate.isNotBlank())
                this@Column.AnimatedVisibility(
                    visible = showFloatingDateHeader,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    FloatingDateHeader(
                        date = lastVisibleDate
                    )
                }
        }
    }
    when (messageAction?.type) {
        ActionType.FOCUS_ATTACHMENTS -> MessageAttachmentsFocused(
            sender = messageAction.message.sender,
            sentAt = messageAction.message.sentAt,
            attachments = messageAction.message.attachments,
            onBackClick = { onDismissMessageAction() }
        )
        else -> Unit
    }
}

@Composable
private fun Header(
    chat: Chat,
    totalActiveParticipants: Int,
    onNavigateBack: () -> Unit,
    onChatClick: (Chat) -> Unit,
    modifier: Modifier = Modifier,
    peerLastActive: Instant? = null,
    dropdownContent: @Composable ColumnScope.() -> Unit
) {
    val chatNameOrId = chat.setting?.name ?: chat.id
    val chatName = @Composable { style: TextStyle ->
        Text(
            text = chatNameOrId,
            style = style.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (chat.type != ChatType.CHAT_ROOM) {
        var dropdownExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val iconButtonSize = 32.dp
            val iconButtonTint = MaterialTheme.colorScheme.primary

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        painter = painterResource(KonnektIcon.chevronLeft),
                        contentDescription = "back",
                        modifier = Modifier.size(iconButtonSize),
                        tint = iconButtonTint
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            onChatClick(chat)
                        },
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarIcon(
                        name = chatNameOrId,
                        iconPath = chat.setting?.iconPath,
                        diameter = 60.dp
                    )
                    Column {
                        chatName(MaterialTheme.typography.bodyLarge)
                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.bodySmall
                        ) {
                            with(ActiveStatus) {
                                when (chat.type) {
                                    ChatType.PERSONAL -> if (peerLastActive != null) Personal(
                                        isActive = totalActiveParticipants > 0,
                                        lastActive = peerLastActive
                                    ) else Text(
                                        text = "Looking for information...",
                                        style = LocalTextStyle.current.copy(
                                            fontStyle = FontStyle.Italic,
                                            color = Gray
                                        )
                                    )
                                    else -> Group(
                                        totalActiveParticipants = totalActiveParticipants
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Box {
                IconButton(
                    onClick = { dropdownExpanded = !dropdownExpanded }
                ) {
                    Icon(
                        painter = painterResource(KonnektIcon.ellipsisVertical),
                        contentDescription = "more",
                        modifier = Modifier.size(iconButtonSize),
                        tint = iconButtonTint
                    )
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    dropdownContent(this)
                }
            }
        }
    } else Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chatName(MaterialTheme.typography.titleSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                with(MaterialTheme.typography.bodySmall) {
                    Icon(
                        painter = painterResource(KonnektIcon.users),
                        contentDescription = "total participants",
                        modifier = Modifier.size(fontSize.value.dp * 1.5f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$totalActiveParticipants Joined",
                        style = this
                    )
                }
            }
        }
        TextButton(
            onClick = onNavigateBack
        ) {
            Text(
                text = "Leave",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Red
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Conversation(
    items: List<ConversationItem>,
    readMarkers: List<UserReadMarker>?,
    chatType: ChatType,
    sentByCurrentUser: (Message) -> Boolean,
    deletedByCurrentUser: (Message) -> Boolean,
    onMessageAction: (MessageAction) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    val myLatestReadMessage = remember(items, readMarkers) {
        items
            .filter {
                it is ConversationItem.MessageItem
                        && sentByCurrentUser(it.message)
            }
            .takeIf { it.isNotEmpty() }
            ?.let {
                if (readMarkers != null && readMarkers.isNotEmpty()) {
                    it.firstOrNull { m ->
                        (m as ConversationItem.MessageItem).message.sentAt <=
                                readMarkers.maxBy { m -> m.lastReadAt }.lastReadAt
                    }
                } else null
            }
    }

    LaunchedEffect(items.size) {
        if (items.size >= 2) {
            val index = state.firstVisibleItemIndex

            if (
                (index == 2
                && items.getOrNull(1) is ConversationItem.DateHeader)
                || index == 1
                || if (items.first() is ConversationItem.MessageItem) {
                    sentByCurrentUser(
                        (items.first() as ConversationItem.MessageItem).message
                    )
                } else false
            ) state.animateScrollToItem(0)
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = state,
        reverseLayout = true,
        verticalArrangement = Arrangement.Top
    ) {
        items(
            count = items.size,
            key = { i -> items[i].key }
        ) {
            when (val item = items[it]) {
                is ConversationItem.DateHeader -> DateHeader(
                    date = item.date,
                    modifier = Modifier.padding(
                        top = 12.dp,
                        bottom = 8.dp
                    )
                )
                is ConversationItem.MessageItem -> {
                    val applyTopPadding = it + 1 < items.size
                        && items[it + 1] is ConversationItem.MessageItem

                    when (chatType) {
                        ChatType.PERSONAL -> {
                            AdjustedMessageBubble(
                                message = item.message,
                                sentByCurrentUser = sentByCurrentUser(item.message),
                                wasSentByPreviousUser = item.wasSentByPreviousUser,
                                deletedByCurrentUser = deletedByCurrentUser(item.message),
                                onAction = onMessageAction,
                                applyTopPadding = applyTopPadding,
                                seenContent = if (myLatestReadMessage == item) {
                                    {
                                        PersonalSeenIndicator()
                                    }
                                } else null
                            )
                        }
                        else -> {
                            val sentByCurrentUser = sentByCurrentUser(item.message)

                            AdjustedMessageBubble(
                                message = item.message,
                                sentByCurrentUser = sentByCurrentUser,
                                wasSentByPreviousUser = item.wasSentByPreviousUser,
                                deletedByCurrentUser = deletedByCurrentUser(item.message),
                                onAction = onMessageAction,
                                applyTopPadding = applyTopPadding,
                                sender = if (sentByCurrentUser) null else item.message.sender,
                                seenContent = if (myLatestReadMessage == item) {
                                    readMarkers?.let { l ->
                                        {
                                            GroupSeenIndicator(
                                                seenBy = l
                                                    .filter { m ->
                                                        m.lastReadAt >= item.message.sentAt
                                                    }
                                                    .sortedByDescending { m -> m.lastReadAt }
                                                    .map(UserReadMarker::user)
                                            )
                                        }
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdjustedMessageBubble(
    message: Message,
    sentByCurrentUser: Boolean,
    wasSentByPreviousUser: Boolean,
    deletedByCurrentUser: Boolean,
    onAction: (MessageAction) -> Unit,
    modifier: Modifier = Modifier,
    sender: User? = null,
    applyTopPadding: Boolean = true,
    seenContent: (@Composable MessageSeenIndicator.() -> Unit)? = null
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = if (applyTopPadding)
                    if (!wasSentByPreviousUser) 16.dp else 4.dp
                else 0.dp
            )
            .combinedClickable(
                onClick = {
                    val visualAttachments = message.attachments.filter {
                        it.type == AttachmentType.IMAGE || it.type == AttachmentType.VIDEO
                    }
                    if (visualAttachments.isNotEmpty()) {
                        onAction(
                            MessageAction(
                                message = message.copy(
                                    attachments = visualAttachments,
                                    sender = message.sender.copy(
                                        username = if (sentByCurrentUser) "Me" else message.sender.username
                                    )
                                ),
                                type = ActionType.FOCUS_ATTACHMENTS
                            )
                        )
                    }
                },
                onLongClick = {
                    onAction(MessageAction(message, ActionType.SHOW_ACTIONS))
                },
                indication = null,
                interactionSource = null
            ),
        contentAlignment = if (sentByCurrentUser) Alignment.CenterEnd
            else Alignment.CenterStart
    ) {
        if (sender == null) MessageBubble(
            message = message,
            sentByCurrentUser = sentByCurrentUser,
            withTail = !wasSentByPreviousUser,
            deletedByCurrentUser = deletedByCurrentUser,
            maxContentWidth = this.maxWidth * 0.9f,
            seenContent = seenContent
        ) else MessageBubble(
            sender = sender,
            message = message,
            sentByCurrentUser = sentByCurrentUser,
            withTail = !wasSentByPreviousUser,
            withAvatar = !wasSentByPreviousUser,
            deletedByCurrentUser = deletedByCurrentUser,
            maxContentWidth = this.maxWidth * 0.9f,
            seenContent = seenContent
        )
    }
}

@Composable
private fun MessageComposer(
    message: String,
    onMessageChange: (String) -> Unit,
    attachments: List<ComposerAttachment>,
    onAddAttachment: (ComposerAttachment) -> Unit,
    action: MessageComposerAction?,
    onActionChange: (MessageComposerAction?) -> Unit,
    onSend: (String) -> Unit,
    sendingMessage: Boolean,
    fileUploadConstraints: FileUploadConstraints,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    val getMultipleContentsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { res ->
        res.forEach {
            try {
                val attachment = context.uriToComposerAttachment(it, fileUploadConstraints)
                Log.d(
                    LOG_TAG,
                    "Add attachment: ${attachment.fileName}, " +
                            "${attachment.type}, " +
                            "with thumbnail: ${attachment.thumbnail != null}"
                )
                onAddAttachment(attachment)
            } catch (e: UriConversionException) {
                e.message?.let(snackbarHostState::showSnackbar)
            } catch (_: Exception) {
                snackbarHostState.showSnackbar("Fail to load file")
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (attachments.isNotEmpty()) ComposerAttachments(
            attachments = attachments,
            modifier = Modifier.fillMaxWidth()
        )
        ShadowedTextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = "Message...",
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    val iconSize = 24.dp
                    fun iconModifier(
                        clickEnabled: Boolean = true,
                        onClick: () -> Unit
                    ) = Modifier
                        .size(iconSize)
                        .clickable(
                            enabled = clickEnabled,
                            indication = null,
                            interactionSource = null,
                            onClick = onClick
                        )

                    Box {
                        Icon(
                            painter = painterResource(KonnektIcon.paperclip),
                            contentDescription = "attachments",
                            modifier = iconModifier {
                                onActionChange(MessageComposerAction.Attachment)
                            }
                        )

                        val density = LocalDensity.current
                        if (action == MessageComposerAction.Attachment) {
                            val startPadding = 48.dp

                            Popup(
                                alignment = Alignment.BottomEnd,
                                offset = with(density) {
                                    IntOffset(
                                        x = startPadding.roundToPx(),
                                        y = -iconSize.roundToPx() - 4.dp.roundToPx()
                                    )
                                },
                                onDismissRequest = {
                                    onActionChange(null)
                                }
                            ) {
                                Attachments(
                                    startPadding = startPadding,
                                    onClick = {
                                        getMultipleContentsLauncher.launch(
                                            when (it) {
                                                AttachmentType.IMAGE -> "image/*"
                                                AttachmentType.VIDEO -> "video/*"
                                                AttachmentType.DOCUMENT -> "application/*"
                                                AttachmentType.AUDIO -> "audio/*"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                    AnimatedContent(
                        targetState = message.isNotEmpty()
                    ) {
                        val enableSendMessage = it || attachments.isNotEmpty()

                        if (!sendingMessage) Icon(
                            painter = painterResource(
                                id = if (enableSendMessage) KonnektIcon.send else KonnektIcon.mic
                            ),
                            contentDescription = "attachments",
                            modifier = iconModifier(
                                clickEnabled = message.isNotBlank() || attachments.isNotEmpty()
                            ) {
                                if (enableSendMessage) onSend(message)
                            }
                        ) else CircularProgressIndicator(
                            modifier = Modifier.size(iconSize),
                            color = Gray,
                            trackColor = DarkGray,
                            strokeWidth = 2.dp
                        )
                    }
                }
            },
            label = if (sendingMessage) "Sending..." else null,
            style = TextFieldDefaults.defaultShadowedStyle(
                labelTextStyle = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    color = Gray
                )
            ),
            singleLine = false,
            maxLines = 5
        )
    }
}

@Composable
private fun ComposerAttachments(
    attachments: List<ComposerAttachment>,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.outline

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val shape = RoundedCornerShape(8.dp)
        val border: Modifier.() -> Modifier = {
            border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
                .clip(shape)
        }

        items(attachments.size) {
            val a = attachments[it]

            if (a.thumbnail != null) Box(
                modifier = Modifier
                    .size(80.dp)
                    .border()
            ) {
                Image(
                    bitmap = a.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .then(
                            if (a.type == AttachmentType.VIDEO) Modifier.blur(2.dp)
                            else Modifier
                        ),
                    contentScale = ContentScale.Crop
                )
                if (a.type == AttachmentType.VIDEO) Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(KonnektIcon.video),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    a.durationSeconds?.let { d ->
                        Text(
                            text = "${d / 60}:${"%02d".format(d % 60)}",
                            modifier = Modifier
                                .align(Alignment.BottomEnd),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else Row(
                modifier = Modifier
                    .border()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(
                        when (a.type) {
                            AttachmentType.IMAGE -> KonnektIcon.image
                            AttachmentType.VIDEO -> KonnektIcon.video
                            AttachmentType.AUDIO -> KonnektIcon.audio
                            AttachmentType.DOCUMENT -> KonnektIcon.file
                        }
                    ),
                    contentDescription = null,
                    tint = borderColor
                )
                Text(
                    text = a.fileName.let {
                        val strings = a.fileName.split('.')
                        val fileNameMax = 10

                        if (strings.size > 1) {
                            val name = strings
                                .take(strings.size - 1)
                                .joinToString(".")
                                .take(fileNameMax)

                            name + if (name.length < fileNameMax) "" else "...." + strings.last()
                        } else with(strings.first()) {
                            take(fileNameMax) + if (length < fileNameMax) "" else "...."
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = borderColor
                    )
                )
            }
        }
    }
}

// bottom end must be placed on top of the icon
@Composable
private fun Attachments(
    startPadding: Dp,
    onClick: (AttachmentType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        val borderWidth = 2.dp
        val primary = MaterialTheme.colorScheme.primary

        Row(
            modifier = Modifier
                .padding(
                    start = startPadding
                )
                .border(
                    color = primary,
                    width = borderWidth
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            attachments.forEach {
                IconButton(
                    onClick = { onClick(it.type) }
                ) {
                    Icon(
                        painter = painterResource(it.iconId),
                        contentDescription = it.type.toString(),
                        tint = it.iconTint
                    )
                }
            }
        }

        val background = MaterialTheme.colorScheme.background
        Canvas(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.End)
                .offset(
                    x = -startPadding,
                    y = -borderWidth * (1.1f)
                )
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2, size.height)
                lineTo(size.width, 0f)
                close()
            }

            drawPath(
                path = path,
                color = background
            )
            drawLine(
                color = primary,
                start = Offset(0f, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = borderWidth.toPx()
            )
            drawLine(
                color = primary,
                start = Offset(size.width, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = borderWidth.toPx()
            )
        }
    }
}

@Composable
private fun DateHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    Text(
        text = date.dateHeaderString(),
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall.copy(
            color = Lime,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun MessageAttachmentsFocused(
    sender: User,
    sentAt: Instant,
    attachments: List<Attachment>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState {
        attachments.size
    }
    val currentPage by remember {
        derivedStateOf {
            pagerState.settledPage
        }
    }
    val statusBarColorManager = LocalStatusBarColorManager.current
    val navigationBarColorManager = LocalNavigationBarColorManager.current
    val systemBarsColor = Color.Black.copy(alpha = 0.5f)

    DisposableEffect(Unit) {
        statusBarColorManager.update(systemBarsColor)
        navigationBarColorManager.update(systemBarsColor)

        onDispose {
            statusBarColorManager.reset()
            navigationBarColorManager.reset()
            MediaPlayerManager.clearPlayback()
        }
    }
    // manually clear playback
    LaunchedEffect(currentPage) {
        Log.d(LOG_TAG, "cur page: $currentPage")
        if (attachments[currentPage].type != AttachmentType.VIDEO)
            MediaPlayerManager.clearPlayback()
    }
    Dialog(
        onDismissRequest = onBackClick,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                val a = attachments[it]

                when (a.type) {
                    AttachmentType.IMAGE -> {
                        val content by rememberResolvedFile(a.path)

                        content?.let { bytes ->
                            Image(
                                bitmap = bytes.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    AttachmentType.VIDEO -> {
                        val content by rememberResolvedFile(a.path)

                        content?.let { bytes ->
                            VideoPlayer(
                                bytes = bytes,
                                key = a.path,
                                controllerBackground = systemBarsColor,
                                autoPlay = currentPage == it
                            )
                        }
                    }
                    else -> Unit
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(systemBarsColor)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onBackClick) {
                    Icon(
                        painter = painterResource(KonnektIcon.chevronLeft),
                        contentDescription = "back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column {
                    Text(
                        text = sender.username,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = sentAt.toDateAndTimeString("dd MMMM yyyy"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }
            Text(
                text = "${pagerState.currentPage + 1}/${attachments.size}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            )
        }
    }
}

@Composable
private fun FloatingDateHeader(
    date: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    val color = Lime

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                color = color,
                shape = shape
            )
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp
            )
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color
            )
        )
    }
}

@Composable
private fun VideoPlayer(
    bytes: ByteArray,
    key: String,
    controllerBackground: Color,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    var showController by remember {
        mutableStateOf(true)
    }
    var progressMs by rememberSaveable {
        mutableLongStateOf(0L)
    }
    var isProgressDragging by remember {
        mutableStateOf(false)
    }
    val playbackState by MediaPlayerManager.playbackState.collectAsState()
    var isPlaying by rememberSaveable {
        mutableStateOf(true)
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        MediaPlayerManager.currentPositionMs.collect {
            progressMs = it
        }
    }
    LaunchedEffect(showController, isProgressDragging, isPlaying) {
        if (
            showController &&
            !isProgressDragging &&
            isPlaying &&
            playbackState != PlaybackState.ENDED
        ) {
            delay(2000)
            showController = false
        }
    }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            MediaPlayerManager.resumeOrPlayMedia(
                context = context,
                mediaBytes = bytes,
                key = key
            )
            showController = false
        } else {
            if (playbackState != PlaybackState.ENDED) {
                showController = true
                MediaPlayerManager.pause()
            }
        }
    }
    LaunchedEffect(playbackState) {
        if (playbackState == PlaybackState.ENDED) {
            isPlaying = false
            showController = true
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = null
            ) {
                showController = !showController
            }
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        /*width=*/ViewGroup.LayoutParams.MATCH_PARENT,
                        /*height=*/ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            },
            modifier = Modifier.align(Alignment.Center),
            update = { view ->
                if (autoPlay) {
                    Log.d(LOG_TAG, "video player updated: $key")
                    MediaPlayerManager.clearPlayback()
                    MediaPlayerManager.resumeOrPlayMedia(
                        context = context,
                        mediaBytes = bytes,
                        key = key,
                        playerView = view
                    )
                }
            }
        )
        AnimatedVisibility(
            visible = showController,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AnimatedContent(
                targetState = isPlaying
            ) {
                Icon(
                    painter = painterResource(
                        id = if (it) KonnektIcon.pause else KonnektIcon.play
                    ),
                    contentDescription = if (it) "pause" else "play",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .clickable {
                            isPlaying = !isPlaying
                        }
                        .padding(16.dp)
                )
            }
        }
        AnimatedVisibility(
            visible = showController,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            VideoPlayerController(
                progressMs = progressMs,
                onProgressChange = MediaPlayerManager::seekTo,
                durationMs = context.getAudioDurationMs(bytes),
                background = controllerBackground,
                onDraggingChange = { isProgressDragging = it }
            )
        }
    }
}

@Composable
private fun VideoPlayerController(
    progressMs: Long,
    durationMs: Long,
    onProgressChange: (Long) -> Unit,
    background: Color,
    onDraggingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(background)
                .padding(16.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProgressBar(
                progress = progressMs.toFloat() / durationMs,
                onProgressChange = {
                    onProgressChange((it * durationMs).toLong())
                },
                modifier = Modifier.fillMaxWidth(),
                onDraggingChange = onDraggingChange
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    Text(msToString(progressMs))
                    Text(msToString(durationMs))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConversationScreenPreview(
    @PreviewParameter(ConversationProvider::class)
    conversation: Conversation
) {
    val user = User(
        id = "user1",
        username = "Kite",
        email = "kite@example.com",
        createdAt = now()
    )
    var messageInput by remember { mutableStateOf("") }
    var composerAction by remember { mutableStateOf<MessageComposerAction?>(null) }

    KonnektTheme {
        Scaffold {
            ConversationScreen(
                currentUser = user,
                chat = conversation.chat,
                messages = conversation.messages,
                readMarkers = null,
                messageInput = messageInput,
                messageAction = null,
                onMessageInputChange = { v -> messageInput = v },
                composerAttachments = emptyList(),
                onAddComposerAttachment = {},
                composerAction = composerAction,
                onComposerActionChange = { a -> composerAction = a },
                onSend = {},
                sendingMessage = false,
                onNavigateBack = {},
                totalActiveParticipants = 0,
                onChatClick = {},
                onMessageAction = {},
                onDismissMessageAction = {},
                fileUploadConstraints = object : FileUploadConstraints {
                    override val maxSizeBytes: Long = 0L
                    override val allowedImageTypes: List<FileType> = listOf()
                    override val allowedVideoTypes: List<FileType> = listOf()
                    override val allowedAudioTypes: List<FileType> = listOf()
                    override val allowedDocumentTypes: List<FileType> = listOf()

                    override fun isMimeTypeAllowed(mimeType: String): AttachmentType? = null

                    override fun isExtensionAllowed(extension: String): AttachmentType? = null
                },
                contentPadding = it,
                modifier = Modifier.padding(it),
                peerLastActive = now()
            )
        }
    }
}