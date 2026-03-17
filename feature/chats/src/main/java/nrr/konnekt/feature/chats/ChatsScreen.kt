package nrr.konnekt.feature.chats

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import nrr.konnekt.core.designsystem.component.OutlinedTextField
import nrr.konnekt.core.designsystem.component.SelectableShadowedButtons
import nrr.konnekt.core.designsystem.component.ShadowedButton
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.GreenPrimaryDarken
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Lime
import nrr.konnekt.core.designsystem.theme.RubikIso
import nrr.konnekt.core.designsystem.util.ButtonDefaults
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.TextFieldErrorIndicator
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.model.UpdateStatus
import nrr.konnekt.core.domain.util.hasLeftByCurrentUser
import nrr.konnekt.core.domain.util.isPersonalChatBlocked
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.upload.util.ValidationResult
import nrr.konnekt.core.network.upload.util.ViolationReason
import nrr.konnekt.core.ui.UriException
import nrr.konnekt.core.ui.component.ActionAlertDialog
import nrr.konnekt.core.ui.component.Alert
import nrr.konnekt.core.ui.component.AlertDialog
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.component.ChatRoomInvitationCard
import nrr.konnekt.core.ui.component.CubicLoading
import nrr.konnekt.core.ui.component.DropdownItem
import nrr.konnekt.core.ui.component.DropdownMenu
import nrr.konnekt.core.ui.component.MemberInvite
import nrr.konnekt.core.ui.component.UserCard
import nrr.konnekt.core.ui.component.chats
import nrr.konnekt.core.ui.component.profilepopup.ProfilePopup
import nrr.konnekt.core.ui.component.profilepopup.toChatPopupData
import nrr.konnekt.core.ui.compositionlocal.LocalFileUploadValidator
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.UiEvent
import nrr.konnekt.core.ui.util.archiveChatAlert
import nrr.konnekt.core.ui.util.blockChatAlert
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.clearChatAlert
import nrr.konnekt.core.ui.util.getFileName
import nrr.konnekt.core.ui.util.leaveChatAlert
import nrr.konnekt.core.ui.util.topRadialGradient
import nrr.konnekt.core.ui.util.unblockChatAlert
import nrr.konnekt.core.ui.util.uriToByteArray
import nrr.konnekt.feature.chats.util.ChatFilter
import nrr.konnekt.feature.chats.util.CreateGroupChatSetting
import nrr.konnekt.feature.chats.util.GroupDropdownItems
import nrr.konnekt.feature.chats.util.PersonDropdownItems

@Composable
internal fun ChatsScreen(
    navigateToConversation: (id: String) -> Unit,
    navigateToTempConversation: (id: String) -> Unit,
    navigateToChatDetail: (chatId: String) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToArchivedChats: () -> Unit,
    navigateToChatInvitations: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ChatsViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle(null)
    val chats by viewModel.chats.collectAsStateWithLifecycle(null)
    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }
    currentUser?.let {
        ChatsScreen(
            user = it,
            chats = chats,
            searchValue = viewModel.searchValue,
            createChatType = viewModel.createChatType,
            userContacts = viewModel.contacts,
            usersByIdentifier = viewModel.usersByIdentifier,
            chatInvitations = viewModel.chatInvitations,
            contentPadding = contentPadding,
            onSearchValueChange = { s -> viewModel.searchValue = s },
            onCreateChatClick = { t -> viewModel.createChatType = t },
            onChatClick = { c -> navigateToConversation(c.id) },
            onChatInfoClick = { c -> navigateToChatDetail(c.id) },
            onArchiveChat = { c ->
                viewModel.updateUserStatus(
                    chat = c,
                    updateArchivedAt = true
                )
            },
            onClearChat = { c ->
                viewModel.updateUserStatus(
                    chat = c,
                    updateClearedAt = true
                )
            },
            onLeaveChat = { c ->
                viewModel.updateUserStatus(
                    chat = c,
                    updateLeftAt = UpdateStatus()
                )
            },
            onBlockChatChange = { c, blocked ->
                viewModel.updateUserStatus(
                    chat = c,
                    updateLeftAt = UpdateStatus(!blocked)
                )
            },
            chatFilter = viewModel.chatFilter,
            onFilterChange = { f -> viewModel.chatFilter = f },
            dismissPopup = {
                viewModel.createChatType = null
                viewModel.usersByIdentifier = null
            },
            createGroupChatSetting = viewModel.createGroupChatSetting,
            onCreateGroupChatSettingChange = { setting ->
                viewModel.createGroupChatSetting = setting
            },
            onCreateGroupChat = {
                viewModel.createGroupChat(navigateToConversation)
            },
            onUserClick = { u ->
                viewModel.getPersonalChat(
                    otherUserId = u.id,
                    complete = { id, exists ->
                        if (exists) navigateToConversation(id)
                        else navigateToTempConversation(id)
                    }
                )
            },
            onUserSearch = viewModel::findUsers,
            onCreateChatRoom = { name, participants ->
                viewModel.createChatRoom(
                    name = name,
                    participants = participants,
                    complete = navigateToConversation
                )
            },
            onAvatarClick = navigateToProfile,
            onChatInvitationBadgeClick = navigateToChatInvitations,
            onArchivedChatsClick = navigateToArchivedChats,
            onRejectChatInvitation = viewModel::rejectChatInvitation,
            onAcceptChatInvitation = { invitation ->
                viewModel.acceptChatInvitation(invitation) { chatId ->
                    navigateToConversation(chatId)
                }
            },
            createActionEnabled = viewModel.createChatActionEnabled,
            modifier = modifier
        )
    }
}

@Composable
private fun ChatsScreen(
    user: User,
    chats: List<LatestChatMessage>?,
    searchValue: String,
    createChatType: ChatType?,
    userContacts: List<User>?,
    usersByIdentifier: List<User>?,
    chatInvitations: List<ChatInvitation>,
    contentPadding: PaddingValues,
    onSearchValueChange: (String) -> Unit,
    onCreateChatClick: (ChatType) -> Unit,
    onChatClick: (Chat) -> Unit,
    onChatInfoClick: (Chat) -> Unit,
    onArchiveChat: (Chat) -> Unit,
    onClearChat: (Chat) -> Unit,
    onLeaveChat: (Chat) -> Unit,
    onBlockChatChange: (Chat, Boolean) -> Unit,
    chatFilter: ChatFilter,
    onFilterChange: (ChatFilter) -> Unit,
    dismissPopup: () -> Unit,
    createGroupChatSetting: CreateGroupChatSetting,
    onCreateGroupChatSettingChange: (CreateGroupChatSetting) -> Unit,
    onCreateGroupChat: () -> Unit,
    onUserSearch: (String) -> Unit,
    onUserClick: (User) -> Unit,
    onCreateChatRoom: (name: String, participants: List<User>) -> Unit,
    onAvatarClick: () -> Unit,
    onArchivedChatsClick: () -> Unit,
    onChatInvitationBadgeClick: () -> Unit,
    onAcceptChatInvitation: (ChatInvitation) -> Unit,
    onRejectChatInvitation: (ChatInvitation) -> Unit,
    createActionEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var alert by retain { mutableStateOf<Alert?>(null) }
    var selectedChat by retain { mutableStateOf<Chat?>(null) }
    val resetSelectedChat = {
        selectedChat = null
    }

    Box(
        modifier = modifier
            .topRadialGradient()
            .bottomRadialGradient()
            .padding(
                top = contentPadding.calculateTopPadding()
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Header(
                user = user,
                chatInvitations = chatInvitations.filterNot { invitation ->
                    invitation.chat.type == ChatType.CHAT_ROOM
                },
                onCreateChatClick = onCreateChatClick,
                onAvatarClick = onAvatarClick,
                onChatInvitationBadgeClick = onChatInvitationBadgeClick,
                modifier = Modifier
                    .padding(
                        start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateRightPadding(LayoutDirection.Ltr)
                    )
                    .padding(horizontal = 8.dp)
            )
            chats?.let {
                Chats(
                    user = user,
                    latestChatMessages = it,
                    chatRoomInvitations = chatInvitations.filter { invitation ->
                        invitation.chat.type == ChatType.CHAT_ROOM &&
                                invitation.chat.deletedAt == null
                    },
                    searchValue = searchValue,
                    chatFilter = chatFilter,
                    isPersonalChatBlocked = { chat ->
                        chat.isPersonalChatBlocked(user)
                    },
                    onChatClick = onChatClick,
                    onChatAvatarClick = { c ->
                        selectedChat = c
                    },
                    onArchiveChat = { c ->
                        alert = archiveChatAlert(c.name()) {
                            onArchiveChat(c)
                        }
                    },
                    onClearChat = { c ->
                        alert = clearChatAlert(c.name()) {
                            onClearChat(c)
                        }
                    },
                    onLeaveChat = { c ->
                        alert = leaveChatAlert(c.name()) {
                            onLeaveChat(c)
                        }
                    },
                    onBlockChatChange = { c, b ->
                        val chatName = c.name()

                        alert = if (!b) unblockChatAlert(chatName) {
                            onBlockChatChange(c, b)
                        } else blockChatAlert(chatName) {
                            onBlockChatChange(c, b)
                        }
                    },
                    onSearchValueChange = onSearchValueChange,
                    onFilterChange = onFilterChange,
                    onArchivedChatsClick = onArchivedChatsClick,
                    onAcceptChatInvitation = onAcceptChatInvitation,
                    onRejectChatInvitation = onRejectChatInvitation,
                    contentPadding = contentPadding
                )
            }
        }
        if (chats?.isEmpty() == true) Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr)
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(KonnektIcon.messageCircleOff),
                contentDescription = "no chats",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "You don't have any chats yet",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            )
        }
        if (chats == null) LoadingChats(
            modifier = Modifier.align(Alignment.Center)
        )
        if (createChatType != null) CreateChatPopup(
            type = createChatType,
            userContacts = userContacts,
            dismiss = dismissPopup,
            createGroupChatSetting = createGroupChatSetting,
            onCreateGroupChatSettingChange = onCreateGroupChatSettingChange,
            onCreateGroupChat = onCreateGroupChat,
            onSearch = onUserSearch,
            onUserClick = onUserClick,
            onCreateChatRoom = onCreateChatRoom,
            usersByIdentifier = usersByIdentifier,
            createActionEnabled = createActionEnabled
        )
        selectedChat?.let { chat ->
            ProfilePopup(
                data = chat.toChatPopupData(),
                onDismissRequest = resetSelectedChat,
                onMessageClick = {
                    onChatClick(chat)
                    resetSelectedChat()
                },
                onInfoClick = {
                    onChatInfoClick(chat)
                    resetSelectedChat()
                }
            )
        }
        ActionAlertDialog(
            alert = alert,
            onDismissRequest = { alert = it }
        )
    }
}

@Composable
private fun Header(
    user: User,
    chatInvitations: List<ChatInvitation>,
    onCreateChatClick: (ChatType) -> Unit,
    onAvatarClick: () -> Unit,
    onChatInvitationBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Konnekt",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontFamily = RubikIso
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            var dropdownExpanded by remember { mutableStateOf(false) }
            val dismissDropdown = { dropdownExpanded = false }
            val createChatWrapper = { type: ChatType ->
                {
                    dismissDropdown()
                    onCreateChatClick(type)
                }
            }
            val iconColor = Lime

            Box(
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onChatInvitationBadgeClick
                )
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.mailbox),
                    contentDescription = "chat invitations",
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
                if (chatInvitations.isNotEmpty()) Box(
                    Modifier
                        .offset(x = 2.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GreenPrimaryDarken)
                        .align(Alignment.TopEnd)
                )
            }
            Box {
                Icon(
                    painter = painterResource(KonnektIcon.add),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            dropdownExpanded = !dropdownExpanded
                        },
                    tint = iconColor
                )
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = dismissDropdown
                ) {
                    DropdownItem(
                        text = "Person",
                        onClick = createChatWrapper(ChatType.PERSONAL),
                        iconId = KonnektIcon.user
                    )
                    DropdownItem(
                        text = "Group",
                        onClick = createChatWrapper(ChatType.GROUP),
                        iconId = KonnektIcon.users
                    )
                    DropdownItem(
                        text = "Chat Room",
                        onClick = createChatWrapper(ChatType.CHAT_ROOM),
                        iconId = KonnektIcon.messageDashed
                    )
                }
            }
            AvatarIcon(
                name = user.username,
                iconPath = user.imagePath,
                diameter = 50.dp,
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onAvatarClick
                )
            )
        }
    }
}

@Composable
private fun LoadingChats(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CubicLoading(
            text = "Loading chats"
        )
    }
}

@Composable
private fun SearchAndArchive(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    onArchivedChatsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val shadowSpace = 4.dp

        OutlinedTextField(
            value = searchValue,
            onValueChange = onSearchValueChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = shadowSpace / 2),
            placeholder = "Search chat...",
            singleLine = true
        )
        ShadowedButton(
            onClick = onArchivedChatsClick,
            modifier = Modifier.fillMaxHeight(),
            style = ButtonDefaults.defaultShadowedStyle(
                contentPadding = PaddingValues(12.dp),
                space = shadowSpace
            )
        ) {
            Icon(
                painter = painterResource(KonnektIcon.archive),
                contentDescription = "archived chats",
                modifier = Modifier
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun TypeFilter(
    selectedFilter: ChatFilter,
    onFilterChange: (ChatFilter) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    SelectableShadowedButtons(
        options = ChatFilter.entries,
        selectedOption = selectedFilter,
        onOptionSelected = onFilterChange,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Chats(
    user: User,
    latestChatMessages: List<LatestChatMessage>,
    chatRoomInvitations: List<ChatInvitation>,
    searchValue: String,
    chatFilter: ChatFilter,
    isPersonalChatBlocked: (Chat) -> Boolean,
    onFilterChange: (ChatFilter) -> Unit,
    onSearchValueChange: (String) -> Unit,
    onChatClick: (Chat) -> Unit,
    onChatAvatarClick: (Chat) -> Unit,
    onArchiveChat: (Chat) -> Unit,
    onClearChat: (Chat) -> Unit,
    onLeaveChat: (Chat) -> Unit,
    onBlockChatChange: (Chat, blocked: Boolean) -> Unit,
    onArchivedChatsClick: () -> Unit,
    onAcceptChatInvitation: (ChatInvitation) -> Unit,
    onRejectChatInvitation: (ChatInvitation) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var showSearchBar by rememberSaveable { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0  && showSearchBar)
                    showSearchBar = false
                else if (available.y > 0 && !showSearchBar)
                    showSearchBar = true

                return Offset.Zero
            }
        }
    }
    val filteredChats = latestChatMessages
        .filter { data ->
            data.chat.participants
                .firstOrNull { participant ->
                    participant.user.id == user.id
                }
                ?.let { participant ->
                    with(participant.status) {
                        archivedAt == null &&
                                (data.chat.type != ChatType.CHAT_ROOM ||
                                        chatFilter == ChatFilter.CHAT_ROOM &&
                                        data.chat.hasLeftByCurrentUser(user))
                    }
                } ?: true
        }
        .run {
            if (chatFilter == ChatFilter.CHAT_ROOM) sortedByDescending { data ->
                data.chat.deletedAt
            } else this
        }

    Column(
        modifier = modifier.nestedScroll(nestedScrollConnection),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(showSearchBar) {
            SearchAndArchive(
                searchValue = searchValue,
                onSearchValueChange = onSearchValueChange,
                onArchivedChatsClick = onArchivedChatsClick,
                modifier = Modifier
                    .padding(
                        start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateRightPadding(LayoutDirection.Ltr)
                    )
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TypeFilter(
                selectedFilter = chatFilter,
                onFilterChange = onFilterChange,
                contentPadding = PaddingValues(
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr)
                )
            )
            if (filteredChats.isNotEmpty()) LazyColumn(
                contentPadding = PaddingValues(
                    bottom = contentPadding.calculateBottomPadding(),
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr)
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (
                    (chatFilter == ChatFilter.ALL ||
                    chatFilter == ChatFilter.CHAT_ROOM) &&
                    chatRoomInvitations.isNotEmpty()
                ) chatRoomInvitations(
                    invitations = chatRoomInvitations,
                    onRejectInvitation = onRejectChatInvitation,
                    onAcceptInvitation = onAcceptChatInvitation
                )

                chats(
                    latestChatMessages = filteredChats,
                    currentUser = user,
                    onClick = onChatClick,
                    onAvatarClick = onChatAvatarClick,
                    dropdownItems = { dismiss, latestChatMessage ->
                        with(latestChatMessage.chat) {
                            when (type) {
                                ChatType.PERSONAL -> PersonDropdownItems(
                                    archived = false,
                                    blocked = isPersonalChatBlocked(this),
                                    dismiss = dismiss,
                                    onArchive = { onArchiveChat(this) },
                                    onClearChat = { onClearChat(this) },
                                    onBlockChange = { onBlockChatChange(this, it) }
                                )
                                ChatType.GROUP -> GroupDropdownItems(
                                    dismiss = dismiss,
                                    archived = false,
                                    onArchive = { onArchiveChat(this) },
                                    onClearChat = { onClearChat(this) },
                                    onLeaveChat = { onLeaveChat(this) }
                                )
                                else -> Unit
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun LazyListScope.chatRoomInvitations(
    invitations: List<ChatInvitation>,
    onRejectInvitation: (ChatInvitation) -> Unit,
    onAcceptInvitation: (ChatInvitation) -> Unit
) {
    items(
        items = invitations,
        key = { it.id }
    ) { invitation ->
        ChatRoomInvitationCard(
            invitation = invitation,
            onJoinChat = onAcceptInvitation,
            onRejectInvitation = onRejectInvitation
        )
    }
}

@Composable
private fun CreateChatPopup(
    type: ChatType,
    userContacts: List<User>?,
    usersByIdentifier: List<User>?,
    dismiss: () -> Unit,
    createGroupChatSetting: CreateGroupChatSetting,
    onCreateGroupChatSettingChange: (CreateGroupChatSetting) -> Unit,
    onCreateGroupChat: () -> Unit,
    onSearch: (username: String) -> Unit,
    onUserClick: (User) -> Unit,
    onCreateChatRoom: (name: String, participants: List<User>) -> Unit,
    createActionEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var userIdentifier by rememberSaveable { mutableStateOf("") }
    val dismissOnAction = { action: () -> Unit ->
        dismiss()
        action()
    }

    AlertDialog(
        onDismissRequest = dismiss,
        modifier = modifier,
        title = when (type) {
            ChatType.PERSONAL -> "Add person"
            ChatType.CHAT_ROOM -> "Create a chat room"
            else -> "Create a new group"
        }
    ) {
        when (type) {
            ChatType.PERSONAL -> SearchUser(
                identifier = userIdentifier,
                onIdentifierChange = { i -> userIdentifier = i },
                onUserClick = { dismissOnAction { onUserClick(it) } },
                onSearch = onSearch,
                users = usersByIdentifier,
                clickUserEnabled = createActionEnabled
            )
            ChatType.CHAT_ROOM -> CreateChatRoom(
                userContacts = userContacts,
                onCreate = { name, participants ->
                    dismissOnAction {
                        onCreateChatRoom(name, participants)
                    }
                },
                enabled = createActionEnabled
            )
            ChatType.GROUP -> CreateGroupChat(
                setting = createGroupChatSetting,
                onSettingChange = onCreateGroupChatSettingChange,
                onCreate = { dismissOnAction(onCreateGroupChat) },
                enabled = createActionEnabled
            )
        }
    }
}

@Composable
private fun SearchUser(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    onSearch: (username: String) -> Unit,
    onUserClick: (User) -> Unit,
    users: List<User>?,
    clickUserEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(identifier) {
        if (identifier.isNotBlank() && identifier.length > 2) {
            delay(1000)
            onSearch(identifier)
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = identifier,
            onValueChange = onIdentifierChange,
            placeholder = "Search by username"
        )
        if (users != null) {
            if (users.isNotEmpty()) Box {
                val state = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 250.dp),
                    state = state,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        count = users.size,
                        key = { users[it].id }
                    ) {
                        UserCard(
                            user = users[it],
                            onClick = onUserClick,
                            enabled = clickUserEnabled
                        )
                    }
                }
                this@Column.AnimatedVisibility(
                    visible = state.canScrollForward,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 16f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(300, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Icon(
                        painter = painterResource(KonnektIcon.chevronsDown),
                        contentDescription = "scroll forward",
                        modifier = Modifier.offset { IntOffset(0, offsetY.toInt()) },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else Text(
                text = "No users found",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkGray,
                    fontStyle = FontStyle.Italic
                )
            )
        } // else loading
    }
}

private fun chatNameConstraints(
    name: String,
    chatType: String
) = listOf(
    TextFieldErrorIndicator(
        error = name.isBlank(),
        message = "$chatType name cannot be empty"
    ),
    TextFieldErrorIndicator(
        error = name.length <= 3,
        message = "$chatType name must be at least 4 characters long"
    )
)

private fun checkNameConstraints(name: String) = chatNameConstraints(name, "")
    .map { it.error }
    .reduce { acc, next -> !acc && !next }

@Composable
private fun CreateChatRoom(
    userContacts: List<User>?,
    onCreate: (name: String, participants: List<User>) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    val selectedContacts = retain { mutableStateListOf<User>() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "Chat room name",
            errorIndicators = chatNameConstraints(
                name = name,
                chatType = "Chat room"
            ),
            singleLine = true
        )
        MemberInvite(
            userContacts = userContacts,
            selectedContacts = selectedContacts,
            onSelectContact = { user, selected ->
                if (selected) selectedContacts.add(user)
                else selectedContacts.remove(user)
            }
        )
        ShadowedButton(
            onClick = {
                onCreate(name, selectedContacts)
                selectedContacts.clear()
            },
            enabled = enabled && checkNameConstraints(name),
        ) {
            Text(text = "Create")
        }
    }
}

@Composable
private fun CreateGroupChat(
    setting: CreateGroupChatSetting,
    onSettingChange: (CreateGroupChatSetting) -> Unit,
    onCreate: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val boxSize = 100.dp
            val color = MaterialTheme.colorScheme.primary
            val context = LocalContext.current
            val fileUploadValidator = LocalFileUploadValidator.current
            val snackbarHostState = LocalSnackbarHostState.current
            val getImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) {
                if (it != null) try {
                    val validationResult = fileUploadValidator(it)
                    if (validationResult is ValidationResult.Invalid) {
                        snackbarHostState.showSnackbar(
                            message = when (validationResult.exception.reason) {
                                ViolationReason.FILE_SIZE_TOO_LARGE ->
                                    "Image size is too large"
                                ViolationReason.FILE_SIZE_INVALID ->
                                    "Can't read image size"
                                ViolationReason.UNSUPPORTED_MIME_TYPE ->
                                    "Mime type is not supported"
                                else -> "Can't resolve image"
                            }
                        )
                        return@rememberLauncherForActivityResult
                    }

                    val content = context.uriToByteArray(it)

                    val fileName = context.getFileName(it)
                    onSettingChange(
                        setting.copy(
                            icon = FileUpload(
                                fileName = fileName,
                                fileExtension = fileName.substringAfterLast('.'),
                                content = content
                            )
                        )
                    )
                } catch (e: UriException) {
                    e.printStackTrace()
                    snackbarHostState.showSnackbar("Can't read image")
                }
            }

            Box(
                modifier = Modifier
                    .size(boxSize)
                    .clip(CircleShape)
                    .clickable {
                        getImageLauncher.launch("image/*")
                    }
            ) {
                if (setting.icon == null) Box {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawArc(
                            color = color,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(
                                width = 4.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(
                                        8.dp.toPx(),
                                        16.dp.toPx()
                                    )
                                )
                            )
                        )
                    }
                    Icon(
                        painter = painterResource(KonnektIcon.image),
                        contentDescription = "add group icon",
                        modifier = Modifier
                            .size(boxSize / 2)
                            .align(Alignment.Center),
                        tint = color
                    )
                } else Image(
                    bitmap = setting.icon.content.decodeToImageBitmap(),
                    contentDescription = "group icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            if (setting.icon == null) Text(
                text = "Add group icon",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color
                )
            )
        }
        OutlinedTextField(
            value = setting.name,
            onValueChange = { onSettingChange(setting.copy(name = it)) },
            placeholder = "New group name",
            singleLine = true,
            errorIndicators = chatNameConstraints(
                name = setting.name,
                chatType = "Group chat"
            )
        )
        ShadowedButton(
            onClick = { onCreate() },
            modifier = Modifier.align(Alignment.End),
            enabled = enabled && checkNameConstraints(setting.name)
        ) {
            Text(text = "Create")
        }
    }
}

@Preview
@Composable
private fun ChatsScreenPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    var createChatType by remember { mutableStateOf<ChatType?>(ChatType.CHAT_ROOM) }

    KonnektTheme {
        Scaffold {
            ChatsScreen(
                chats = data.latestChatMessages,
                user = data.user,
                searchValue = "",
                createChatType = createChatType,
                userContacts = emptyList(),
                usersByIdentifier = mutableListOf<User>().apply {
                    repeat(10) { i ->
                        add(data.user.copy(id = "$i"))
                    }
                }.toList(),
                chatInvitations = emptyList(),
                contentPadding = PaddingValues(16.dp),
                onSearchValueChange = {},
                onCreateChatClick = { t ->
                    createChatType = t
                },
                onChatClick = {},
                onChatInfoClick = {},
                onArchiveChat = {},
                onClearChat = {},
                onLeaveChat = {},
                onBlockChatChange = { _, _ -> },
                chatFilter = ChatFilter.ALL,
                onFilterChange = {},
                dismissPopup = { createChatType = null },
                createGroupChatSetting = CreateGroupChatSetting(),
                onCreateGroupChatSettingChange = {},
                onCreateGroupChat = {},
                onUserClick = {},
                onUserSearch = {},
                onCreateChatRoom = { _, _ -> },
                onAvatarClick = {},
                onArchivedChatsClick = {},
                onChatInvitationBadgeClick = {},
                onRejectChatInvitation = {},
                onAcceptChatInvitation = {},
                createActionEnabled = true,
                modifier = Modifier.padding(it),
            )
        }
    }
}