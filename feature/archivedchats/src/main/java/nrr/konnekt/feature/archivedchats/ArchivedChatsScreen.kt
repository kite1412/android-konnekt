package nrr.konnekt.feature.archivedchats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.model.UpdateStatus
import nrr.konnekt.core.domain.util.hasLeftByCurrentUser
import nrr.konnekt.core.domain.util.isPersonalChatBlocked
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.component.ActionAlertDialog
import nrr.konnekt.core.ui.component.Alert
import nrr.konnekt.core.ui.component.CubicLoading
import nrr.konnekt.core.ui.component.SimpleHeader
import nrr.konnekt.core.ui.component.chats
import nrr.konnekt.core.ui.component.profilepopup.ProfilePopup
import nrr.konnekt.core.ui.component.profilepopup.toChatPopupData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.ChatDropdownItems
import nrr.konnekt.core.ui.util.blockChatAlert
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.clearChatAlert
import nrr.konnekt.core.ui.util.leaveChatAlert
import nrr.konnekt.core.ui.util.unarchiveChatAlert
import nrr.konnekt.core.ui.util.unblockChatAlert

@Composable
internal fun ArchivedChatsScreen(
    navigateBack: () -> Unit,
    navigateToConversation: (id: String) -> Unit,
    navigateToChatDetail: (chatId: String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ArchivedChatsViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val archivedChats by viewModel.archivedChats.collectAsStateWithLifecycle()

    currentUser?.let { currentUser ->
        ArchivedChatsScreen(
            currentUser = currentUser,
            archivedChats = archivedChats,
            contentPadding = contentPadding,
            onNavigateBack = navigateBack,
            onChatClick = { c -> navigateToConversation(c.id) },
            onChatInfoClick = { c -> navigateToChatDetail(c.id) },
            onUnarchive = { chat ->
                viewModel.updateChatParticipantStatus(
                    chatId = chat.id,
                    unarchive = true
                )
            },
            onClearChat = { chat ->
                viewModel.updateChatParticipantStatus(
                    chatId = chat.id,
                    updateClearAt = true
                )
            },
            onBlockChange = { chat, blocked ->
                viewModel.updateChatParticipantStatus(
                    chatId = chat.id,
                    updateLeftAt = UpdateStatus(!blocked)
                )
            },
            onLeaveChat = { chat ->
                viewModel.updateChatParticipantStatus(
                    chatId = chat.id,
                    updateLeftAt = UpdateStatus()
                )
            },
            modifier = modifier
        )
    }
}

@Composable
private fun ArchivedChatsScreen(
    currentUser: User,
    archivedChats: List<LatestChatMessage>?,
    contentPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    onChatClick: (Chat) -> Unit,
    onChatInfoClick: (Chat) -> Unit,
    onUnarchive: (Chat) -> Unit,
    onClearChat: (Chat) -> Unit,
    onBlockChange: (chat: Chat, blocked: Boolean) -> Unit,
    onLeaveChat: (Chat) -> Unit,
    modifier: Modifier = Modifier
) {
    var alert by retain { mutableStateOf<Alert?>(null) }
    var selectedChat by retain { mutableStateOf<Chat?>(null) }
    val resetSelectedChat = {
        selectedChat = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .bottomRadialGradient()
            .padding(contentPadding),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleHeader(
                title = "Archived Chats",
                onNavigateBack = onNavigateBack
            )
            if (!archivedChats.isNullOrEmpty()) LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = contentPadding.calculateBottomPadding(),
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr)
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chats(
                    latestChatMessages = archivedChats,
                    currentUser = currentUser,
                    onClick = { onChatClick(it.chat) },
                    onAvatarClick = { selectedChat = it }
                ) { dismiss, latestChatMessage ->
                    val chat = latestChatMessage.chat
                    val blocked = chat.isPersonalChatBlocked(currentUser)

                    if (blocked) ChatDropdownItems.Unblock(
                        dismiss = dismiss,
                        onBlockChange = { blocked ->
                            alert = unblockChatAlert(chat.name()) {
                                onBlockChange(chat, blocked)
                            }
                        }
                    )
                    ChatDropdownItems.Unarchive(
                        dismiss = dismiss,
                        onUnarchive = {
                            alert = unarchiveChatAlert(chat.name()) {
                                onUnarchive(chat)
                            }
                        }
                    )
                    ChatDropdownItems.ClearChat(
                        dismiss = dismiss,
                        onClearChat = {
                            alert = clearChatAlert(chat.name()) {
                                onClearChat(chat)
                            }
                        }
                    )
                    if (!chat.hasLeftByCurrentUser(currentUser) && chat.type != ChatType.PERSONAL) ChatDropdownItems.Leave(
                        dismiss = dismiss,
                        onLeaveChat = {
                            alert = leaveChatAlert(chat.name()) {
                                onLeaveChat(chat)
                            }
                        }
                    )
                    if (!blocked && chat.type == ChatType.PERSONAL) ChatDropdownItems.Block(
                        dismiss = dismiss,
                        onBlockChange = { blocked ->
                            alert = blockChatAlert(chat.name()) {
                                onBlockChange(chat, blocked)
                            }
                        }
                    )
                }
            }
        }
        if (archivedChats?.isEmpty() == true) Text(
            text = "You don't have any archived chats.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Gray
            ),
            modifier = Modifier.align(Alignment.Center)
        )
        if (archivedChats == null) CubicLoading(
            text = "Loading chats",
            modifier = Modifier.align(Alignment.Center)
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

@Preview
@Composable
private fun ArchivedChatsPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    KonnektTheme {
        Scaffold { paddingValues ->
            ArchivedChatsScreen(
                currentUser = data.user,
                archivedChats = emptyList(),
                contentPadding = paddingValues,
                onNavigateBack = {},
                onChatClick = {},
                onChatInfoClick = {},
                onUnarchive = {},
                onClearChat = {},
                onBlockChange = { _, _ -> },
                onLeaveChat = {}
            )
        }
    }
}