package nrr.konnekt.feature.archivedchats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.util.isDeletedByCurrentUser
import nrr.konnekt.core.domain.util.isPersonalChatBlocked
import nrr.konnekt.core.domain.util.isSentByCurrentUser
import nrr.konnekt.core.domain.util.isUnreadByCurrentUser
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.component.ActionAlertDialog
import nrr.konnekt.core.ui.component.Alert
import nrr.konnekt.core.ui.component.CubicLoading
import nrr.konnekt.core.ui.component.chats
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.ChatDropdownItems
import nrr.konnekt.core.ui.util.bottomRadialGradient

@Composable
internal fun ArchivedChatsScreen(
    navigateBack: () -> Unit,
    navigateToConversation: (id: String) -> Unit,
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
            onChatClick = { latestChatMessage ->
                navigateToConversation(latestChatMessage.chat.id)
            },
            onUnarchive = { chatId ->
                viewModel.updateChatParticipantStatus(
                    chatId = chatId,
                    unarchive = true
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
    onChatClick: (LatestChatMessage) -> Unit,
    onUnarchive: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var alert by retain { mutableStateOf<Alert?>(null) }

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
            Header(
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
                    onClick = onChatClick,
                    sentByCurrentUser = {
                        it.message?.isSentByCurrentUser(currentUser) == true
                    },
                    unreadByCurrentUser = {
                        it.isUnreadByCurrentUser(currentUser)
                    },
                    deletedByCurrentUser = {
                        it.message?.isDeletedByCurrentUser(currentUser) == true
                    },
                    blockedByCurrentUser = {
                        it.chat.isPersonalChatBlocked(currentUser)
                    },
                ) { dismiss, latestChatMessage ->
                    ChatDropdownItems.Unarchive(
                        dismiss = dismiss,
                        onUnarchive = {
                            alert = Alert(
                                onConfirm = { onUnarchive(latestChatMessage.chat.id) },
                                title = "Unarchive Chat",
                                message = latestChatMessage.chat.setting?.name?.let {
                                    "Unarchive ${it}?"
                                }
                            )
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
        ActionAlertDialog(
            alert = alert,
            onDismissRequest = { alert = it }
        )
    }
}

@Composable
private fun Header(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textStyle = MaterialTheme.typography.titleMedium

            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.chevronLeft),
                    contentDescription = "back",
                    modifier = Modifier.size(textStyle.fontSize.value.dp)
                )
            }
            Text(
                text = "Archived Chats",
                style = textStyle.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
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
                onUnarchive = {}
            )
        }
    }
}