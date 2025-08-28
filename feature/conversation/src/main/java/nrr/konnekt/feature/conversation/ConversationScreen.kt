package nrr.konnekt.feature.conversation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
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
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Lime
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.component.DropdownMenu
import nrr.konnekt.core.ui.component.MessageBubble
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.previewparameter.Conversation
import nrr.konnekt.core.ui.previewparameter.ConversationProvider
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.topRadialGradient
import nrr.konnekt.feature.conversation.util.ActiveStatus
import nrr.konnekt.feature.conversation.util.ConversationItem
import nrr.konnekt.feature.conversation.util.MessageComposerAction
import nrr.konnekt.feature.conversation.util.UiEvent
import nrr.konnekt.feature.conversation.util.attachments
import nrr.konnekt.feature.conversation.util.dateHeaderString
import nrr.konnekt.feature.conversation.util.mapToConversationItem
import kotlin.time.Instant

@Composable
internal fun ConversationScreen(
    navigateBack: () -> Unit,
    navigateToChatDetail: (Chat) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(null)
    val chat by viewModel.chat.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle(null)
    val totalActiveParticipants by viewModel.totalActiveParticipants.collectAsStateWithLifecycle()
    val peerLastActive by viewModel.peerLastActive.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val messageInput = viewModel.messageInput

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
                    messageInput = messageInput,
                    onMessageInputChange = { viewModel.messageInput = it },
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
    messageInput: String,
    onMessageInputChange: (String) -> Unit,
    composerAction: MessageComposerAction?,
    onComposerActionChange: (MessageComposerAction?) -> Unit,
    onSend: (String) -> Unit,
    sendingMessage: Boolean,
    onNavigateBack: () -> Unit,
    onChatClick: (Chat) -> Unit,
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
                val conversationItems = messages.mapToConversationItem()
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
                    chatType = chat.type,
                    sentByCurrentUser = { m -> m.sender.id == currentUser.id },
                    deletedByCurrentUser = { m ->
                        m.messageStatuses
                            .firstOrNull { it.userId == currentUser.id }
                            ?.isDeleted == true
                    },
                    modifier = Modifier.weight(1f),
                    state = state
                )
                MessageComposer(
                    message = messageInput,
                    onMessageChange = onMessageInputChange,
                    action = composerAction,
                    onActionChange = onComposerActionChange,
                    onSend = onSend,
                    sendingMessage = sendingMessage
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
    chatType: ChatType,
    sentByCurrentUser: (Message) -> Boolean,
    deletedByCurrentUser: (Message) -> Boolean,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
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
                        ChatType.PERSONAL -> AdjustedMessageBubble(
                            message = item.message,
                            sentByCurrentUser = sentByCurrentUser(item.message),
                            wasSentByPreviousUser = item.wasSentByPreviousUser,
                            deletedByCurrentUser = deletedByCurrentUser(item.message),
                            applyTopPadding = applyTopPadding
                        )
                        else -> {
                            val sentByCurrentUser = sentByCurrentUser(item.message)

                            AdjustedMessageBubble(
                                message = item.message,
                                sentByCurrentUser = sentByCurrentUser,
                                wasSentByPreviousUser = item.wasSentByPreviousUser,
                                deletedByCurrentUser = deletedByCurrentUser(item.message),
                                applyTopPadding = applyTopPadding,
                                sender = if (sentByCurrentUser) null else item.message.sender
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
    modifier: Modifier = Modifier,
    sender: User? = null,
    applyTopPadding: Boolean = true
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = if (applyTopPadding)
                    if (!wasSentByPreviousUser) 16.dp else 4.dp
                else 0.dp
            ),
        contentAlignment = if (sentByCurrentUser) Alignment.CenterEnd
            else Alignment.CenterStart
    ) {
        if (sender == null) MessageBubble(
            message = message,
            sentByCurrentUser = sentByCurrentUser,
            withTail = !wasSentByPreviousUser,
            deletedByCurrentUser = deletedByCurrentUser,
            maxContentWidth = this.maxWidth * 0.9f
        ) else MessageBubble(
            sender = sender,
            message = message,
            sentByCurrentUser = sentByCurrentUser,
            withTail = !wasSentByPreviousUser,
            withAvatar = !wasSentByPreviousUser,
            deletedByCurrentUser = deletedByCurrentUser,
            maxContentWidth = this.maxWidth * 0.9f
        )
    }
}

@Composable
private fun MessageComposer(
    message: String,
    onMessageChange: (String) -> Unit,
    action: MessageComposerAction?,
    onActionChange: (MessageComposerAction?) -> Unit,
    onSend: (String) -> Unit,
    sendingMessage: Boolean,
    modifier: Modifier = Modifier
) {
    val getMultipleContentsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { r ->

    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
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
                        Icon(
                            painter = painterResource(
                                id = if (it) KonnektIcon.send else KonnektIcon.mic
                            ),
                            contentDescription = "attachments",
                            modifier = iconModifier(
                                clickEnabled = !sendingMessage && message.isNotBlank()
                            ) {
                                if (it) onSend(message)
                            }
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
                messageInput = messageInput,
                onMessageInputChange = { v -> messageInput = v },
                composerAction = composerAction,
                onComposerActionChange = { a -> composerAction = a },
                onSend = {},
                sendingMessage = false,
                onNavigateBack = {},
                totalActiveParticipants = 0,
                onChatClick = {},
                contentPadding = it,
                modifier = Modifier.padding(it),
                peerLastActive = now()
            )
        }
    }
}