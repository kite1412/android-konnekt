package nrr.konnekt.feature.chatdetail

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nrr.konnekt.core.designsystem.component.OutlinedTextField
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.component.Toggle
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.DarkNavy
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.GreenPrimaryDarken
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldStyle
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.domain.model.UpdateStatus
import nrr.konnekt.core.domain.util.isPersonalChatBlocked
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.component.ActionAlertDialog
import nrr.konnekt.core.ui.component.Alert
import nrr.konnekt.core.ui.component.AlertDialog
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.component.ChatHeader
import nrr.konnekt.core.ui.component.profilepopup.ProfilePopup
import nrr.konnekt.core.ui.component.profilepopup.toChatPopupData
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.previewparameter.Conversation
import nrr.konnekt.core.ui.previewparameter.ConversationProvider
import nrr.konnekt.core.ui.util.UiEvent
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.blockChatAlert
import nrr.konnekt.core.ui.util.getLetterColor
import nrr.konnekt.core.ui.util.rememberResolvedFile
import nrr.konnekt.core.ui.util.unblockChatAlert
import nrr.konnekt.feature.chatdetail.navigation.navigateToChatDetail
import nrr.konnekt.feature.chatdetail.navigation.navigateToTempPersonalChatDetail
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
internal fun ChatDetailScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToConversation: (isChatId: Boolean, id: String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    val chat by viewModel.chat.collectAsStateWithLifecycle(null)
    val activeParticipants by viewModel.activeParticipants.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is UiEvent.NavigateBack -> navigateBack()
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(
                    message = it.message
                )
            }
        }
    }
    chat?.let { chat ->
        currentUser?.let { currentUser ->
            ChatDetailScreen(
                chat = chat,
                currentUser = currentUser,
                activeParticipants = activeParticipants,
                peerLastActiveAt = viewModel.peerLastActiveAt,
                currentUserContacts = viewModel.currentUserContacts,
                peerGroupsInCommon = viewModel.peerGroupsInCommon,
                chatInvitations = viewModel.chatInvitations,
                onNavigateBack = navigateBack,
                onShare = {},
                onDescChange = {},
                onClearChat = {
                    viewModel.updateChatParticipantStatus(
                        updateClearedAt = true
                    )
                },
                onLeaveChat = {
                    viewModel.updateChatParticipantStatus(
                        updateLeftAt = UpdateStatus()
                    )
                },
                isParticipantActive = { participant ->
                    activeParticipants.any { activeParticipant ->
                        activeParticipant.user.id == participant.user.id
                    }
                },
                onParticipantInfoClick = { participant ->
                    scope.launch {
                        val chatId = viewModel.getPersonalChatId(participant)

                        chatId?.let { chatId ->
                            navController.navigateToChatDetail(chatId)
                        } ?: navController.navigateToTempPersonalChatDetail(participant.id)
                    }
                },
                onParticipantMessageClick = { participant ->
                    scope.launch {
                        val chatId = viewModel.getPersonalChatId(participant)

                        navigateToConversation(chatId != null, chatId ?: participant.id)
                    }
                },
                onBlockChange = { blocked ->
                    viewModel.updateChatParticipantStatus(
                        updateLeftAt = UpdateStatus(!blocked)
                    )
                },
                onAddMemberClick = viewModel::updateCurrentUserContacts,
                onAddMembers = { users ->
                    viewModel.inviteToChat(users.map(User::id))
                },
                onCancelInvitation = { invitation ->
                    viewModel.cancelInvitations(listOf(invitation.id))
                },
                modifier = modifier.padding(contentPadding),
                isPersonalChatAdded = viewModel.isPersonalChatAdded
            )
        }
    }
}

@Composable
private fun ChatDetailScreen(
    chat: Chat,
    currentUser: User,
    activeParticipants: List<ChatParticipant>,
    peerGroupsInCommon: List<Chat>,
    peerLastActiveAt: Instant?,
    currentUserContacts: List<User>?,
    chatInvitations: List<ChatInvitation>,
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    onDescChange: (String) -> Unit,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit,
    isParticipantActive: (ChatParticipant) -> Boolean,
    onParticipantInfoClick: (User) -> Unit,
    onParticipantMessageClick: (User) -> Unit,
    onBlockChange: (Boolean) -> Unit,
    onAddMemberClick: () -> Unit,
    onAddMembers: (selectedContacts: List<User>) -> Unit,
    onCancelInvitation: (ChatInvitation) -> Unit,
    modifier: Modifier = Modifier,
    canEditDesc: Boolean = false,
    isPersonalChatAdded: Boolean = false,
    pushNotificationEnabled: Boolean = false,
    onPushNotificationChange: (Boolean) -> Unit = {}
) {
    var alert by retain { mutableStateOf<Alert?>(null) }
    var selectedParticipant by retain { mutableStateOf<User?>(null) }
    var isClickingPendingInvitation by retain { mutableStateOf(false) }
    var showAddMemberDialog by retain { mutableStateOf(false) }
    val resetSelectedParticipant = {
        selectedParticipant = null
        isClickingPendingInvitation = false
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            chat = chat,
            peerLastActive = peerLastActiveAt,
            totalActiveParticipants = activeParticipants.size - 1,
            onNavigateBack = onNavigateBack,
            onShare = onShare
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ChatInfo(
                    chat = chat,
                    currentUser = currentUser,
                    isAdmin = chat.participants.any { participant ->
                        participant.user.id == currentUser.id &&
                                participant.role == ParticipantRole.ADMIN
                    },
                    chatInvitations = chatInvitations,
                    canEditDesc = canEditDesc,
                    onDescChange = onDescChange,
                    isPersonalChatAdded = isPersonalChatAdded,
                    peerGroupsInCommon = peerGroupsInCommon,
                    messageNotificationEnabled = pushNotificationEnabled,
                    onMessageNotificationChange = onPushNotificationChange,
                    onClearChat = {
                        alert = Alert(
                            onConfirm = onClearChat,
                            title = "Clear Chat",
                            message = "Clear all messages from this chat?"
                        )
                    },
                    onLeaveChat = {
                        alert = Alert(
                            onConfirm = onLeaveChat,
                            title = "Leave Chat",
                            message = "Leave this chat?"
                        )
                    },
                    onAddMember = {
                        showAddMemberDialog = true
                        onAddMemberClick()
                    },
                    onDeleteGroup = {},
                    onChatParticipantClick = { participant ->
                        selectedParticipant = participant.user
                    },
                    isParticipantActive = isParticipantActive,
                    onBlockChange = { blocked ->
                        val chatName = chat.name()

                        alert = if (blocked) blockChatAlert(
                            chatName = chatName,
                            onConfirm = { onBlockChange(blocked) }
                        ) else unblockChatAlert(
                            chatName = chatName,
                            onConfirm = { onBlockChange(blocked) }
                        )
                    },
                    onInvitationClick = { invitation ->
                        selectedParticipant = invitation.receiver
                        isClickingPendingInvitation = true
                    }
                )
            }
        }
    }

    selectedParticipant?.let {
        ProfilePopup(
            data = it.toChatPopupData(),
            onDismissRequest = resetSelectedParticipant,
            onInfoClick = {
                onParticipantInfoClick(it)
                resetSelectedParticipant()
            },
            onMessageClick = {
                onParticipantMessageClick(it)
                resetSelectedParticipant()
            }
        ) { action ->
            if (isClickingPendingInvitation) action(
                KonnektIcon.x,
                "Cancel Invitation",
                Red
            ) {
                alert = Alert(
                    onConfirm = {
                        onCancelInvitation(
                            chatInvitations.first { invitation ->
                                invitation.receiver.id == it.id
                            }
                        )
                        resetSelectedParticipant()
                    },
                    title = "Cancel Invitation",
                    message = "Cancel invitation for ${it.username}?"
                )
            }
        }
    }
    if (showAddMemberDialog) AddMemberDialog(
        chatParticipants = chat.participants,
        existingInvitations = chatInvitations,
        userContacts = currentUserContacts,
        onDismissRequest = { showAddMemberDialog = false },
        onConfirm = onAddMembers
    )
    ActionAlertDialog(
        alert = alert,
        onDismissRequest = { alert = it }
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Header(
    chat: Chat,
    totalActiveParticipants: Int,
    peerLastActive: Instant?,
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
                .weight(1f),
            peerLastActive = peerLastActive
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
    currentUser: User,
    isAdmin: Boolean,
    chatInvitations: List<ChatInvitation>,
    onClearChat: () -> Unit,
    canEditDesc: Boolean,
    onDescChange: (String) -> Unit,
    isPersonalChatAdded: Boolean,
    peerGroupsInCommon: List<Chat>,
    messageNotificationEnabled: Boolean,
    onMessageNotificationChange: (Boolean) -> Unit,
    onLeaveChat: () -> Unit,
    onAddMember: () -> Unit,
    onDeleteGroup: () -> Unit,
    onChatParticipantClick: (ChatParticipant) -> Unit,
    isParticipantActive: (ChatParticipant) -> Boolean,
    onBlockChange: (Boolean) -> Unit,
    onInvitationClick: (ChatInvitation) -> Unit,
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
                    isBlocked = chat.isPersonalChatBlocked(currentUser),
                    groupsInCommon = peerGroupsInCommon,
                    pushNotificationEnabled = messageNotificationEnabled,
                    onPushNotificationChange = onMessageNotificationChange,
                    onClearChat = onClearChat,
                    onBlockChange = onBlockChange
                )
                ChatType.GROUP -> GroupChatInfo(
                    isAdmin = isAdmin,
                    currentUser = currentUser,
                    permissionSettings = chat.setting?.permissionSettings ?: ChatPermissionSettings(),
                    participants = chat.participants.sortedBy { participant ->
                        participant.role != ParticipantRole.ADMIN
                    },
                    chatInvitations = chatInvitations,
                    messageNotificationEnabled = messageNotificationEnabled,
                    onMessageNotificationChange = onMessageNotificationChange,
                    onClearChat = onClearChat,
                    onLeaveChat = onLeaveChat,
                    onAddMember = onAddMember,
                    onDeleteGroup = onDeleteGroup,
                    onChatParticipantClick = onChatParticipantClick,
                    onInvitationClick = onInvitationClick,
                    isParticipantActive = isParticipantActive
                )
                else -> Unit
            }
        }
    }
}

@Composable
private fun PersonalChatInfo(
    isAdded: Boolean,
    isBlocked: Boolean,
    groupsInCommon: List<Chat>,
    pushNotificationEnabled: Boolean,
    onPushNotificationChange: (Boolean) -> Unit,
    onClearChat: () -> Unit,
    onBlockChange: (Boolean) -> Unit
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
        isBlocked = isBlocked,
        onBlockChange = onBlockChange,
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
    if (isBlocked) ActionUnblockChat {
        onBlockChange(false)
    }
    if (isAdded) onClearChat?.let {
        ActionClearChat(onClick = it)
    }
    if (!isBlocked) ActionBlockChat {
        onBlockChange(true)
    }
}

@Composable
private fun GroupChatInfo(
    isAdmin: Boolean,
    currentUser: User,
    permissionSettings: ChatPermissionSettings,
    participants: List<ChatParticipant>,
    chatInvitations: List<ChatInvitation>,
    messageNotificationEnabled: Boolean,
    onMessageNotificationChange: (Boolean) -> Unit,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit,
    onAddMember: () -> Unit,
    onChatParticipantClick: (ChatParticipant) -> Unit,
    isParticipantActive: (ChatParticipant) -> Boolean,
    onInvitationClick: (ChatInvitation) -> Unit,
    onDeleteGroup: () -> Unit
) {
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
    }
    ChatInfoSection("Members") {
        ChatParticipants(
            currentUser = currentUser,
            participants = participants,
            isParticipantActive = isParticipantActive,
            onClick = onChatParticipantClick
        )
    }
    if (chatInvitations.isNotEmpty()) ChatInfoSection("Pending Invitations") {
        ChatInvitations(
            invitations = chatInvitations,
            onInvitationClick = onInvitationClick
        )
    }
    GroupChatActions(
        isAdmin = isAdmin,
        canLeave = participants.firstOrNull { participant ->
            participant.user.id == currentUser.id
        }?.status?.leftAt == null,
        onClearChat = onClearChat,
        permissionSettings = permissionSettings,
        onLeaveChat = onLeaveChat,
        onAddMember = onAddMember,
        onDeleteGroup = onDeleteGroup
    )
}

@Composable
private fun GroupChatActions(
    isAdmin: Boolean,
    canLeave: Boolean,
    permissionSettings: ChatPermissionSettings,
    onClearChat: () -> Unit,
    onLeaveChat: () -> Unit,
    onAddMember: () -> Unit,
    onDeleteGroup: () -> Unit,
    modifier: Modifier = Modifier,
) = ActionsLayout(modifier = modifier) {
    if (isAdmin || permissionSettings.manageMembers) {
        val contentColor = MaterialTheme.colorScheme.primary

        Action(
            iconId = KonnektIcon.userAdd,
            name = "Add Member",
            onClick = onAddMember,
            contentColor = contentColor
        )
    }
    ActionClearChat(onClick = onClearChat)
    if (canLeave) Action(
        iconId = KonnektIcon.logOut,
        name = "Leave",
        onClick = onLeaveChat,
        modifier = modifier
    )
    if (isAdmin && !canLeave) Action(
        iconId = KonnektIcon.delete,
        name = "Delete Group",
        onClick = onDeleteGroup
    )
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
    verticalArrangement = Arrangement.spacedBy(8.dp),
    content = actions
)

@Composable
private fun ActionUnblockChat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) = Action(
    iconId = KonnektIcon.eye,
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
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = name,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun ChatParticipants(
    currentUser: User,
    participants: List<ChatParticipant>,
    onClick: (ChatParticipant) -> Unit,
    isParticipantActive: (ChatParticipant) -> Boolean,
    modifier: Modifier = Modifier
) {
    var showAll by retain { mutableStateOf(false) }

    AnimatedContent(
        targetState = showAll,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DarkNavy,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        val take = 5
        val needMore = participants.size > take

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            participants
                .take(if (it && needMore) Int.MAX_VALUE else take)
                .forEach { participant ->
                    ChatParticipantCard(
                        participant = participant,
                        isActive = isParticipantActive(participant),
                        onClick = onClick,
                        clickEnabled = currentUser.id != participant.user.id
                    )
                }

            if (needMore) {
                val rotationDegrees by animateFloatAsState(
                    targetValue = if (!it) -90f else -270f
                )

                Icon(
                    painter = painterResource(KonnektIcon.chevronLeft),
                    contentDescription = if (it) "show less" else "show more",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            showAll = !showAll
                        }
                        .rotate(rotationDegrees),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ChatInvitations(
    invitations: List<ChatInvitation>,
    onInvitationClick: (ChatInvitation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        invitations.forEach { invitation ->
            val user = invitation.receiver

            Contact(
                user = user,
                selected = false,
                added = false,
                invited = true,
                enabled = true,
                onClick = { _, _ -> onInvitationClick(invitation) }
            )
        }
    }
}

@Composable
private fun ChatParticipantCard(
    participant: ChatParticipant,
    isActive: Boolean,
    clickEnabled: Boolean,
    onClick: (ChatParticipant) -> Unit,
    modifier: Modifier = Modifier
) {
    val activityColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary
            else DarkGray
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = activityColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .clickable(
                enabled = clickEnabled,
                interactionSource = null,
                indication = null
            ) { onClick(participant) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1.4f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val user = participant.user

            Box {
                AvatarIcon(
                    name = user.username,
                    iconPath = user.imagePath
                )
                Box(
                    Modifier
                        .size(12.dp)
                        .background(
                            color = if (isActive) GreenPrimaryDarken else DarkGray,
                            shape = CircleShape
                        )
                        .align(Alignment.BottomEnd)
                )
            }
            Column {
                Text(
                    text = user.username,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                user.bio?.let { bio ->
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DarkGray,
                            fontStyle = FontStyle.Italic
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (participant.role == ParticipantRole.ADMIN) Text(
            text = "Admin",
            modifier = Modifier.weight(0.2f),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun AddMemberDialog(
    chatParticipants: List<ChatParticipant>,
    existingInvitations: List<ChatInvitation>,
    userContacts: List<User>?,
    onDismissRequest: () -> Unit,
    onConfirm: (selectedContacts: List<User>) -> Unit,
    modifier: Modifier = Modifier
) {
    var usernameSearch by retain { mutableStateOf("") }
    val selectedContacts = retain {
        mutableStateListOf<User>()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = "Add Member",
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedContacts)
                    onDismissRequest()
                },
                enabled = selectedContacts.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        cancelButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "Cancel",
                    color = Red
                )
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val style = MaterialTheme.typography.bodyLarge.copy(
                color = DarkGray,
                fontStyle = FontStyle.Italic
            )

            when {
                userContacts == null -> Text(
                    text = "Loading contacts...",
                    style = style
                )
                userContacts.isNotEmpty() -> Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = usernameSearch,
                        onValueChange = { usernameSearch = it },
                        placeholder = "Search by username",
                        singleLine = true
                    )
                    LazyColumn(
                        modifier = Modifier
                            .sizeIn(
                                maxHeight = 400.dp
                            )
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = userContacts.filter { contact ->
                                contact.username.contains(usernameSearch, ignoreCase = true)
                            },
                            key = { u -> u.id }
                        ) { user ->
                            val added = chatParticipants.any { participant ->
                                participant.user.id == user.id
                            }
                            val invited = existingInvitations.any { invitation ->
                                invitation.receiver.id == user.id
                            }

                            Contact(
                                user = user,
                                selected = selectedContacts.any { u ->
                                    u.id == user.id
                                },
                                added = added,
                                invited = invited,
                                enabled = !added && !invited,
                                onClick = { user, selected ->
                                    if (selected) selectedContacts.add(user)
                                    else selectedContacts.remove(user)
                                }
                            )
                        }
                    }
                }
                else -> Text(
                    text = "No contacts found",
                    style = style
                )
            }
        }
    }
}

@Composable
private fun Contact(
    user: User,
    selected: Boolean,
    added: Boolean,
    invited: Boolean,
    enabled: Boolean,
    onClick: (User, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
            else DarkGray
    )
    val onClick = {
        onClick(user, !selected)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AvatarIcon(
                name = user.username,
                iconPath = user.imagePath
            )
            Text(
                text = user.username,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier.weight(0.2f)
        ) {
            if (invited) Text(
                text = "Invited",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Gray,
                    fontWeight = FontWeight.Bold
                )
            ) else if (added) Text(
                text = "Added",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ) else RadioButton(
                selected = selected,
                onClick = onClick
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
                    ),
                    participants = mutableListOf<ChatParticipant>().apply {
                        (0 until 3).forEach { _ ->
                            addAll(
                                conversation.chat.participants.map { participant ->
                                    participant.copy(
                                        user = participant.user.copy(
                                            bio = "A very long long long long long long long long long bio"
                                        ),
                                        role = ParticipantRole.ADMIN
                                    )
                                }
                            )
                        }
                    }
                ),
                currentUser = conversation.chat.participants.first().user,
                activeParticipants = emptyList(),
                peerLastActiveAt = null,
                currentUserContacts = emptyList(),
                chatInvitations = emptyList(),
                onNavigateBack = {},
                onShare = {},
                onDescChange = {},
                onClearChat = {},
                onLeaveChat = {},
                isParticipantActive = { true },
                onParticipantInfoClick = {},
                onParticipantMessageClick = {},
                onBlockChange = {},
                onAddMemberClick = {},
                onAddMembers = {},
                onCancelInvitation = {},
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