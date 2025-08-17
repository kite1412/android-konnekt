package nrr.konnekt.feature.conversation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.component.DropdownMenu
import nrr.konnekt.core.ui.previewparameter.Conversation
import nrr.konnekt.core.ui.previewparameter.ConversationProvider
import nrr.konnekt.feature.conversation.util.ActiveStatus
import kotlin.time.Instant

@Composable
internal fun ConversationScreen(
    navigateBack: () -> Unit,
    navigateToChatDetail: (Chat) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val chat by viewModel.chat.collectAsStateWithLifecycle()

    chat?.let {
        ConversationScreen(
            chat = it,
            // TODO
            totalActiveParticipants = 2 ,
            onNavigateBack = navigateBack,
            onChatClick = navigateToChatDetail,
            modifier = modifier,
            // TODO
            peerLastActive = now()
        )
    }
}

@Composable
private fun ConversationScreen(
    chat: Chat,
    totalActiveParticipants: Int,
    onNavigateBack: () -> Unit,
    onChatClick: (Chat) -> Unit,
    modifier: Modifier = Modifier,
    peerLastActive: Instant? = null
) {
    Column(
        modifier = modifier
    ) {
        Header(
            chat = chat,
            totalActiveParticipants = totalActiveParticipants,
            onNavigateBack = onNavigateBack,
            onChatClick = onChatClick,
            peerLastActive = peerLastActive
        ) {

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
            modifier = modifier.fillMaxWidth(),
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

@Preview
@Composable
private fun ConversationScreenPreview(
    @PreviewParameter(ConversationProvider::class)
    conversation: Conversation
) {
    KonnektTheme {
        Scaffold {
            ConversationScreen(
                chat = conversation.chat,
                onNavigateBack = {},
                totalActiveParticipants = 0,
                onChatClick = {},
                modifier = Modifier.padding(it),
                peerLastActive = now()
            )
        }
    }
}