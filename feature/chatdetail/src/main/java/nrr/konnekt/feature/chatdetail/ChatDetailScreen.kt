package nrr.konnekt.feature.chatdetail

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.component.Toggle
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldStyle
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Event
import nrr.konnekt.core.model.util.toDateAndTimeString
import nrr.konnekt.core.ui.component.ChatHeader
import nrr.konnekt.core.ui.previewparameter.Conversation
import nrr.konnekt.core.ui.previewparameter.ConversationProvider
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.getLetterColor
import nrr.konnekt.core.ui.util.rememberResolvedFile
import nrr.konnekt.feature.chatdetail.util.UiEvent
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
internal fun ChatDetailScreen(
    navigateBack: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    val chat by viewModel.chat.collectAsStateWithLifecycle()
    val totalActiveParticipants by viewModel.totalChatParticipants.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    chat?.let { chat ->
        ChatDetailScreen(
            chat = chat,
            totalActiveParticipants = totalActiveParticipants ?: 0,
            onNavigateBack = navigateBack,
            onShare = {},
            onDescChange = {},
            modifier = modifier.padding(contentPadding),
            isPersonalChatAdded = viewModel.isPersonalChatAdded
        )
    }
}

@Composable
private fun ChatDetailScreen(
    chat: Chat,
    totalActiveParticipants: Int,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    onDescChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    canEditDesc: Boolean = false,
    isPersonalChatAdded: Boolean = false,
    peerGroupsInCommon: List<Chat> = emptyList(),
    pushNotificationEnabled: Boolean = false,
    onPushNotificationChange: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            chat = chat,
            totalActiveParticipants = totalActiveParticipants,
            onNavigateBack = onNavigateBack,
            onShare = onShare
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ChatInfo(
                    chat = chat,
                    isAdmin = true,
                    canEditDesc = canEditDesc,
                    onDescChange = onDescChange,
                    isPersonalChatAdded = isPersonalChatAdded,
                    peerGroupsInCommon = peerGroupsInCommon,
                    messageNotificationEnabled = pushNotificationEnabled,
                    onMessageNotificationChange = onPushNotificationChange,
                    onClearChat = {},
                    eventNotificationEnabled = true,
                    onEventNotificationChange = {},
                    onLeaveChat = {},
                    onAddMember = {},
                    onCreateEvent = {},
                    onDeleteGroup = {},
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Header(
    chat: Chat,
    totalActiveParticipants: Int,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatHeader(
            chatName = chat.setting?.name ?: chat.id,
            chatIconPath = chat.setting?.iconPath,
            chatType = chat.type,
            totalActiveParticipants = totalActiveParticipants,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        if (chat.type == ChatType.PERSONAL) IconButton(
            onClick = onShare
        ) {
            Icon(
                painter = painterResource(KonnektIcon.share),
                contentDescription = "share",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun chatInfoTitleStyle(): TextStyle =
    MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold
    )

@Composable
private fun ChatInfo(
    chat: Chat,
    isAdmin: Boolean,
    onClearChat: () -> Unit,
    canEditDesc: Boolean,
    onDescChange: (String) -> Unit,
    isPersonalChatAdded: Boolean,
    peerGroupsInCommon: List<Chat>,
    messageNotificationEnabled: Boolean,
    onMessageNotificationChange: (Boolean) -> Unit,
    eventNotificationEnabled: Boolean,
    onEventNotificationChange: (Boolean) -> Unit,
    onLeaveChat: () -> Unit,
    onAddMember: () -> Unit,
    onCreateEvent: () -> Unit,
    onDeleteGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        var descEdit by rememberSaveable(chat.setting?.description) {
            mutableStateOf(chat.setting?.description ?: "")
        }
        var editDesc by rememberSaveable { mutableStateOf(false) }
        val editable = chat.type == ChatType.GROUP && canEditDesc
        val editEnabled = editable && editDesc
        val focusRequester = remember { FocusRequester() }

        AdjustedShadowedTextField(
            value = descEdit,
            onValueChange = {
                descEdit = it
            },
            label = if (chat.type == ChatType.PERSONAL) "Bio" else "Group Description",
            enabled = editEnabled,
            placeholder = if (chat.type == ChatType.PERSONAL) "No Bio" else "No Description",
            actions = if (editable) {
                {
                    LaunchedEffect(editDesc) {
                        Log.d("feat:detail", editDesc.toString())
                        if (!editDesc) {
                            descEdit = chat.setting?.description ?: ""
                            focusRequester.freeFocus()
                        } else focusRequester.requestFocus()
                    }
                    AnimatedContent(
                        targetState = editDesc
                    ) { state ->
                        val transition = updateTransition(state, "ui state transition")

                        val tint by transition.animateColor(
                            label = "tint color"
                        ) { s ->
                            if (!s) MaterialTheme.colorScheme.primary else Red
                        }

                        Icon(
                            painter = painterResource(if (!state) KonnektIcon.pencil else KonnektIcon.x),
                            contentDescription = "edit",
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null
                            ) {
                                editDesc = !editDesc
                            },
                            tint = tint
                        )
                    }
                }
            } else null,
            style = TextFieldDefaults.defaultShadowedStyle(
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalContentColor.current,
                    fontStyle = if (descEdit.isEmpty()) FontStyle.Italic else FontStyle.Normal
                )
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (editEnabled && descEdit != chat.setting?.description)
                        onDescChange(descEdit)
                    editDesc = false
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            focusRequester = focusRequester
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            when (chat.type) {
                ChatType.PERSONAL -> PersonalChatInfo(
                    isAdded = isPersonalChatAdded,
                    groupsInCommon = peerGroupsInCommon,
                    pushNotificationEnabled = messageNotificationEnabled,
                    onPushNotificationChange = onMessageNotificationChange,
                    onClearChat = onClearChat
                )
                ChatType.GROUP -> GroupChatInfo(
                    isAdmin = isAdmin,
                    events = chat.events,
                    participants = chat.participants,
                    messageNotificationEnabled = messageNotificationEnabled,
                    onMessageNotificationChange = onMessageNotificationChange,
                    eventNotificationEnabled = eventNotificationEnabled,
                    onClearChat = onClearChat,
                    onLeaveChat = onLeaveChat,
                    onAddMember = onAddMember,
                    onCreateEvent = onCreateEvent,
                    onDeleteGroup = onDeleteGroup,
                    onEventNotificationChange = onEventNotificationChange
                )
                else -> Unit
            }
        }
    }
}

@Composable
private fun PersonalChatInfo(
    isAdded: Boolean,
    groupsInCommon: List<Chat>,
    pushNotificationEnabled: Boolean,
    onPushNotificationChange: (Boolean) -> Unit,
    onClearChat: () -> Unit
) {
    if (isAdded) ToggleSetting(
        desc = "Message Notifications",
        checked = pushNotificationEnabled,
        onCheckedChange = onPushNotificationChange
    )
    if (groupsInCommon.isNotEmpty()) ChatInfoSection(
        title = "${groupsInCommon.size} Groups in Common"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconSize = 50.dp
            val maxShow = 5

            groupsInCommon.take(maxShow).forEachIndexed { i, chat ->
                SmallChatIcon(
                    chatName = chat.setting?.name ?: chat.id,
                    iconPath = chat.setting?.iconPath,
                    modifier = Modifier.offset {
                        IntOffset(
                            x = -(iconSize / 3 * i).roundToPx(),
                            y = 0
                        )
                    },
                    iconSize = iconSize
                )
            }
            if (groupsInCommon.size > maxShow) Text(
                text = "${groupsInCommon.size - maxShow}+",
                modifier = Modifier.offset {
                    IntOffset(
                        x = -((maxShow - 1) * (iconSize / 3) - 8.dp).roundToPx(),
                        y = 0
                    )
                },
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (iconSize.value / 3).sp,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
    PersonalChatActions(
        isAdded = isAdded,
        isBlocked = false,
        onBlockChange = {  },
        onClearChat = onClearChat
    )
}

@Composable
private fun PersonalChatActions(
    isAdded: Boolean,
    isBlocked: Boolean,
    onBlockChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onClearChat: (() -> Unit)? = null
) = ActionsLayout(modifier) {
    if (isAdded) onClearChat?.let {
        ActionClearChat(onClick = it)
    }
    if (isBlocked) ActionUnblockChat {
        onBlockChange(false)
    } else ActionBlockChat {
        onBlockChange(true)
    }
}

@Composable
private fun GroupChatInfo(
    isAdmin: Boolean,
    events: List<Event>,
    participants: List<ChatParticipant>,
    messageNotificationEnabled: Boolean,
    onMessageNotificationChange: (Boolean) -> Unit,
    eventNotificationEnabled: Boolean,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit,
    onAddMember: () -> Unit,
    onCreateEvent: () -> Unit,
    onDeleteGroup: () -> Unit,
    onEventNotificationChange: (Boolean) -> Unit
) {
    if (events.isNotEmpty()) ChatInfoSection(
        title = "Events",
        titleStyle = chatInfoTitleStyle().copy(
            color = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            events.forEach {
                GroupEvent(
                    title = it.title,
                    startsAt = it.startsAt,
                    desc = it.description
                )
            }
        }
    }
    ChatInfoSection(
        title = "Notifications",
        titleStyle = chatInfoTitleStyle().copy(
            color = Gray
        )
    ) {
        ToggleSetting(
            desc = "Messages",
            checked = messageNotificationEnabled,
            onCheckedChange = onMessageNotificationChange
        )
        ToggleSetting(
            desc = "Events",
            checked = eventNotificationEnabled,
            onCheckedChange = onEventNotificationChange
        )
    }
    GroupChatActions(
        isAdmin = isAdmin,
        onClearChat = onClearChat,
        onLeaveChat = onLeaveChat,
        onAddMember = onAddMember,
        onCreateEvent = onCreateEvent,
        onDeleteGroup = onDeleteGroup
    )
}

@Composable
private fun GroupChatActions(
    isAdmin: Boolean,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit,
    onAddMember: () -> Unit,
    onCreateEvent: () -> Unit,
    onDeleteGroup: () -> Unit,
    modifier: Modifier = Modifier,
) = ActionsLayout(modifier = modifier) {
    if (isAdmin) {
        val contentColor = MaterialTheme.colorScheme.primary

        Action(
            iconId = KonnektIcon.userAdd,
            name = "Add Member",
            onClick = onAddMember,
            contentColor = contentColor
        )
        Action(
            iconId = KonnektIcon.calendar,
            name = "Create Event",
            onClick = onCreateEvent,
            contentColor = contentColor
        )
    }
    ActionClearChat(onClick = onClearChat)
    Action(
        iconId = KonnektIcon.logOut,
        name = "Leave",
        onClick = onLeaveChat,
        modifier = modifier
    )
    if (isAdmin) Action(
        iconId = KonnektIcon.delete,
        name = "Delete Group",
        onClick = onDeleteGroup
    )
}

@Composable
private fun GroupEvent(
    title: String,
    startsAt: Instant,
    desc: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(KonnektIcon.calendar),
            contentDescription = "event",
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(
                text = startsAt.toDateAndTimeString("dd MMMM yyyy"),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DarkGray,
                    fontStyle = FontStyle.Italic
                )
            )
            Text(
                text = title,
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            desc?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Gray
                    )
                )
            }
        }
    }
}

@Composable
private fun ChatInfoSection(
    title: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = chatInfoTitleStyle(),
    content: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text(
        text = title,
        style = titleStyle
    )
    content(this)
}

@Composable
private fun SmallChatIcon(
    chatName: String,
    iconPath: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp
) {
    val shape = CircleShape
    val border: @Composable Modifier.() -> Modifier = {
        border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.background,
            shape = shape
        )
    }

    iconPath?.let {
        val chatIcon by rememberResolvedFile(it)

        chatIcon?.let { bytes ->
            Image(
                bitmap = bytes.asImageBitmap(),
                contentDescription = "group icon",
                modifier = modifier
                    .size(iconSize)
                    .clip(shape)
                    .border()
            )
        } ?: ChatNameIcon(
            chatName = chatName,
            size = iconSize,
            clipShape = shape,
            modifier = modifier.border()
        )
    } ?: ChatNameIcon(
        chatName = chatName,
        size = iconSize,
        clipShape = shape,
        modifier = modifier.border()
    )
}

@Composable
private fun ChatNameIcon(
    chatName: String,
    size: Dp,
    clipShape: Shape,
    modifier: Modifier = Modifier
) {
    val firstLetter = chatName.first()

    Box(
        modifier = modifier
            .size(size)
            .clip(clipShape)
            .background(firstLetter.getLetterColor()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = firstLetter.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// TODO: inline it in ChatInfo
@Composable
private fun AdjustedShadowedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    placeholder: String = "",
    style: ShadowedTextFieldStyle = TextFieldDefaults.defaultShadowedStyle(),
    actions: (@Composable () -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester? = null
) {
    ShadowedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        placeholder = placeholder,
        label = label,
        style = style.copy(
            labelTextStyle = MaterialTheme.typography.bodySmall.copy(
                color = Gray
            )
        ),
        actions = actions,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        focusRequester = focusRequester
    )
}

@Composable
private fun ToggleSetting(
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = desc,
            style = chatInfoTitleStyle()
        )
        Toggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun ActionsLayout(
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    content = actions
)

@Composable
private fun ActionUnblockChat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Action(
    iconId = KonnektIcon.circleOff,
    name = "Unblock",
    onClick = onClick,
    modifier = modifier,
    contentColor = MaterialTheme.colorScheme.primary
)

@Composable
private fun ActionBlockChat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Action(
    iconId = KonnektIcon.circleOff,
    name = "Block",
    onClick = onClick,
    modifier = modifier,
    contentColor = Red
)

@Composable
private fun ActionClearChat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Action(
    iconId = KonnektIcon.messageCircleX,
    name = "Clear Chat",
    onClick = onClick,
    modifier = modifier,
    contentColor = Red
)

@Composable
private fun Action(
    iconId: Int,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Red
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = name,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = name,
                style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ChatDetailScreenPreview(
    @PreviewParameter(ConversationProvider::class)
    conversation: Conversation
) {
    KonnektTheme {
        Scaffold {
            ChatDetailScreen(
                chat = conversation.chat.copy(
                    setting = conversation.chat.setting?.copy(
                        description = null
                    )
                ),
                totalActiveParticipants = 1,
                onNavigateBack = {},
                onShare = {},
                onDescChange = {},
                modifier = Modifier.padding(it),
                isPersonalChatAdded = true,
                pushNotificationEnabled = false,
                onPushNotificationChange = {},
                peerGroupsInCommon = mutableListOf<Chat>().apply {
                    repeat(6) {
                        add(conversation.chat)
                    }
                }
            )
        }
    }
}