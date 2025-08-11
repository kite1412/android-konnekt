package nrr.konnekt.feature.chats

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nrr.konnekt.core.designsystem.component.OutlinedTextField
import nrr.konnekt.core.designsystem.component.ShadowedButton
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.RubikIso
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.component.DropdownItem
import nrr.konnekt.core.ui.component.DropdownMenu
import nrr.konnekt.core.ui.component.chats
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.getLetterColor
import nrr.konnekt.core.ui.util.rememberResolvedImage
import nrr.konnekt.core.ui.util.topRadialGradient
import nrr.konnekt.feature.chats.util.GroupDropdownItems
import nrr.konnekt.feature.chats.util.PersonDropdownItems

@Composable
internal fun ChatsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ChatsViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val chats by viewModel.chats.collectAsStateWithLifecycle(emptyList())

    currentUser?.let {
        ChatsScreen(
            user = it,
            chats = chats,
            searchValue = viewModel.searchValue,
            contentPadding = contentPadding,
            onSearchValueChange = { s -> viewModel.searchValue = s },
            onChatClick = {},
            onArchiveChat = {},
            onClearChat = {},
            onLeaveChat = {},
            onBlockChat = {},
            modifier = modifier
        )
    }
}

@Composable
private fun ChatsScreen(
    user: User,
    chats: List<LatestChatMessage>,
    searchValue: String,
    contentPadding: PaddingValues,
    onSearchValueChange: (String) -> Unit,
    onChatClick: (Chat) -> Unit,
    onArchiveChat: (Chat) -> Unit,
    onClearChat: (Chat) -> Unit,
    onLeaveChat: (Chat) -> Unit,
    onBlockChat: (Chat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .topRadialGradient()
            .bottomRadialGradient()
            .padding(
                start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateRightPadding(LayoutDirection.Ltr),
                top = contentPadding.calculateTopPadding()
            ),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Header(
            user = user,
            onCreateChatClick = {},
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Toolbar(
            searchValue = searchValue,
            onSearchValueChange = onSearchValueChange
        )
        Chats(
            user = user,
            chats = chats,
            onChatClick = onChatClick,
            bottomContentPadding = contentPadding.calculateBottomPadding(),
            onArchiveChat = onArchiveChat,
            onClearChat = onClearChat,
            onLeaveChat = onLeaveChat,
            onBlockChat = onBlockChat
        )
    }
}

@Composable
private fun Header(
    user: User,
    onCreateChatClick: (ChatType) -> Unit,
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
                    tint = MaterialTheme.colorScheme.primary
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
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                user.imagePath?.let {
                    val avatar by rememberResolvedImage(it)

                    avatar?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "avatar",
                            modifier = Modifier.fillMaxSize(),
                            alignment = Alignment.Center
                        )
                    }
                } ?: with(user.username.first()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(getLetterColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = this@with.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
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
                placeholder = "Search chat..."
            )
            ShadowedButton(
                onClick = {},
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(12.dp),
                space = shadowSpace
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
}

@Composable
private fun Chats(
    user: User,
    chats: List<LatestChatMessage>,
    onChatClick: (Chat) -> Unit,
    bottomContentPadding: Dp,
    onArchiveChat: (Chat) -> Unit,
    onClearChat: (Chat) -> Unit,
    onLeaveChat: (Chat) -> Unit,
    onBlockChat: (Chat) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            bottom = bottomContentPadding
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chats(
            latestChatMessages = chats,
            onClick = { onChatClick(it.chat) },
            sentByCurrentUser = {
                user.id == it.messageDetail?.sender?.id
            },
            unreadByCurrentUser = {
                it.messageDetail != null
                    && user.id != it.messageDetail?.sender?.id
                    && with(
                    it.messageDetail
                        ?.messageStatuses
                        ?.firstOrNull { s ->
                            s.userId == user.id
                        }
                    ) {
                        (this == null || readAt == null)
                    }
            },
            dropdownItems = { dismiss, latestChatMessage ->
                with(latestChatMessage.chat) {
                    when (type) {
                        ChatType.PERSONAL -> PersonDropdownItems(
                            archived = false,
                            blocked = false,
                            dismiss = dismiss,
                            onArchive = { onArchiveChat(this) },
                            onClearChat = { onClearChat(this) },
                            onBlockChange = { onBlockChat(this) }
                        )
                        ChatType.GROUP -> GroupDropdownItems(
                            dismiss = dismiss,
                            archived = false,
                            onArchive = { onArchiveChat(this) },
                            onClearChat = { onClearChat(this) },
                            onLeaveChat = { onLeaveChat(this) }
                        )
                        else -> null
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun ChatsScreenPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData,
) {
    KonnektTheme {
        Scaffold {
            ChatsScreen(
                chats = data.latestChatMessages,
                user = data.user,
                searchValue = "",
                contentPadding = PaddingValues(16.dp),
                onSearchValueChange = {},
                onChatClick = {},
                onArchiveChat = {},
                onClearChat = {},
                onLeaveChat = {},
                onBlockChat = {},
                modifier = Modifier.padding(it)
            )
        }
    }
}