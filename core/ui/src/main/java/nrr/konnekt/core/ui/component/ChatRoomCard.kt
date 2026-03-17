package nrr.konnekt.core.ui.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.component.ShadowedBox
import nrr.konnekt.core.designsystem.theme.Cyan
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.GreenPrimaryDarken
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedBoxDefaults
import nrr.konnekt.core.domain.util.hasLeftByCurrentUser
import nrr.konnekt.core.domain.util.isDeleted
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider

@Composable
fun ChatRoomCard(
    chat: Chat,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val expired = chat.isDeleted() || chat.hasLeftByCurrentUser(currentUser)

    if (expired) {
        ShadowedBox(
            modifier = modifier.fillMaxWidth(),
            style = shadowedBoxStyle(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Hero(
                    chatName = chat.name(),
                    description = if (chat.deletedAt != null) "Chat room dismissed."
                    else "You have left this chat.",
                    modifier = Modifier.weight(1f)
                ) {
                    val textStyle = LocalTextStyle.current

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(KonnektIcon.users),
                            contentDescription = "total participant",
                            modifier = Modifier.size(textStyle.fontSize.value.dp * 1.5f)
                        )
                        Text(
                            text = chat.participants.size.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall,
                    LocalContentColor provides Gray
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        chat.deletedAt?.let { deletedAt ->
                            Text("Dismissed at")
                            Text(timeString(deletedAt))
                        } ?: chat.participants.firstOrNull { participant ->
                            participant.user.id == currentUser.id
                        }?.let { participant ->
                            participant.status.leftAt?.let { leftAt ->
                                Text("Left at")
                                Text(timeString(leftAt))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRoomInvitationCard(
    invitation: ChatInvitation,
    onRejectInvitation: (ChatInvitation) -> Unit,
    onJoinChat: (ChatInvitation) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedBg by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = Cyan,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    ShadowedBox(
        modifier = modifier.fillMaxWidth(),
        style = shadowedBoxStyle(animatedBg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Hero(
                chatName = invitation.chat.name(),
                description = "You are invited to this chat room.",
                modifier = Modifier.weight(1f)
            ) {
                val textStyle = LocalTextStyle.current

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        painter = painterResource(KonnektIcon.userAdd),
                        contentDescription = "inviter",
                        modifier = Modifier.size(textStyle.fontSize.value.dp * 1.5f)
                    )
                    Text(
                        text = invitation.inviter.username,
                        style = textStyle.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Join?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modifier = { onClick: () -> Unit ->
                        Modifier
                            .size(32.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = onClick
                            )
                    }

                    Icon(
                        painter = painterResource(KonnektIcon.x),
                        contentDescription = "reject",
                        modifier = modifier {
                            onRejectInvitation(invitation)
                        },
                        tint = Red
                    )
                    Icon(
                        painter = painterResource(KonnektIcon.check),
                        contentDescription = "join",
                        modifier = modifier {
                            onJoinChat(invitation)
                        },
                        tint = GreenPrimaryDarken
                    )
                }
            }
        }
    }
}

@Composable
private fun Hero(
    chatName: String,
    description: String,
    modifier: Modifier = Modifier,
    additionalInfo: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(KonnektIcon.messageDashed),
            contentDescription = "chat room",
            modifier = Modifier.size(48.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val bodySmall = MaterialTheme.typography.bodySmall

            Column {
                Text(
                    text = chatName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = bodySmall
                )
            }
            CompositionLocalProvider(
                LocalContentColor provides Gray,
                LocalTextStyle provides bodySmall
            ) {
                additionalInfo?.invoke()
            }
        }
    }
}

@Composable
private fun shadowedBoxStyle(bg: Color) =
    ShadowedBoxDefaults.defaultStyle(
        contentPadding = PaddingValues(16.dp),
        space = 6.dp,
        backgroundColor = bg,
        shadowColor = MaterialTheme.colorScheme.onPrimary
    )

@Preview
@Composable
private fun ChatRoomCardPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    KonnektTheme {
        Column {
            ChatRoomCard(
                chat = data.latestChatMessages.first().chat.copy(
                    deletedAt = now(),
                    participants = listOf(data.latestChatMessages.first().chat.participants.first())
                ),
                currentUser = data.latestChatMessages.first().chat.participants.first().user
            )
            ChatRoomInvitationCard(
                invitation = data.chatInvitations.first(),
                onRejectInvitation = {},
                onJoinChat = {}
            )
        }
    }
}