package nrr.konnekt.core.ui.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.component.ShadowedButton
import nrr.konnekt.core.designsystem.theme.Cyan
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.ButtonDefaults
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.util.info
import nrr.konnekt.core.model.util.toStringFormatted
import nrr.konnekt.core.model.util.toStringIgnoreSecond
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider

fun LazyListScope.chats(
    latestChatMessages: List<LatestChatMessage>,
    onClick: (LatestChatMessage) -> Unit,
    sentByCurrentUser: (LatestChatMessage) -> Boolean,
    unreadByCurrentUser: (LatestChatMessage) -> Boolean,
    deletedByCurrentUser: (LatestChatMessage) -> Boolean,
    dropdownItems: (@Composable ColumnScope.(dismiss: () -> Unit, LatestChatMessage) -> Unit)? = null
) {
    items(
        count = latestChatMessages.size,
        key = { i -> latestChatMessages[i].chat.id }
    ) {
        with(latestChatMessages[it]) {
            ChatCard(
                latestChatMessage = this,
                onClick = onClick,
                sentByCurrentUser = sentByCurrentUser(this),
                unreadByCurrentUser = unreadByCurrentUser(this),
                deletedByCurrentUser = deletedByCurrentUser(this),
                dropdownItems = dropdownItems?.let { c ->
                    { dismiss ->
                        c(this, dismiss, this@with)
                    }
                }
            )
        }
    }
}

@Composable
private fun ChatCard(
    latestChatMessage: LatestChatMessage,
    onClick: (LatestChatMessage) -> Unit,
    sentByCurrentUser: Boolean,
    unreadByCurrentUser: Boolean,
    deletedByCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    iconDiameter: Dp = 40.dp,
    dropdownItems: (@Composable ColumnScope.(dismiss: () -> Unit) -> Unit)? = null
) {
    var expandDropdown by remember { mutableStateOf(false) }
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

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onPrimary
    ) {
        ShadowedButton(
            onClick = { onClick(latestChatMessage) },
            modifier = modifier.fillMaxWidth(),
            style = ButtonDefaults.defaultShadowedStyle(
                contentPadding = PaddingValues(
                    horizontal = 10.dp,
                    vertical = 16.dp
                ),
                space = 6.dp,
                backgroundColor = if (unreadByCurrentUser) animatedBg else MaterialTheme.colorScheme.primary,
            ),
            bounceBack = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                with(latestChatMessage) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvatarIcon(
                            name = chat.setting?.name ?: chat.id,
                            iconPath = chat.setting?.iconPath,
                            diameter = iconDiameter
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = chat.setting?.name ?: chat.id,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            CompositionLocalProvider(
                                LocalTextStyle provides MaterialTheme.typography.bodySmall
                            ) {
                                message?.let { m ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = buildAnnotatedString {
                                                if (sentByCurrentUser) withStyle(
                                                    style = SpanStyle(
                                                        fontStyle = FontStyle.Italic
                                                    )
                                                ) {
                                                    append("You: ")
                                                } else append("${m.sender.username}: ")
                                            }
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            m.attachments
                                                .takeIf { m.content.isBlank() }
                                                ?.firstOrNull()
                                                ?.let { a ->
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
                                                        modifier = Modifier.size(14.dp),
                                                        tint = DarkGray
                                                    )
                                                }
                                            Text(
                                                text = buildAnnotatedString {
                                                    val nonContentStyle = { block: AnnotatedString.Builder.() -> Unit ->
                                                        withStyle(
                                                            style = SpanStyle(
                                                                fontStyle = FontStyle.Italic,
                                                                color = DarkGray
                                                            ),
                                                            block = block
                                                        )
                                                    }

                                                    if (m.content.isNotBlank()) {
                                                        if (!m.isHidden) {
                                                            if (!deletedByCurrentUser) withStyle(
                                                                style = SpanStyle(
                                                                    color = Color.White
                                                                )
                                                            ) {
                                                                append(m.content)
                                                            } else nonContentStyle {
                                                                append("You deleted this message")
                                                            }
                                                        } else nonContentStyle {
                                                            append("Message has been deleted")
                                                        }
                                                    } else if (m.attachments.isNotEmpty()) nonContentStyle {
                                                        append(
                                                            when (m.attachments.first().type) {
                                                                AttachmentType.IMAGE -> "Image"
                                                                AttachmentType.VIDEO -> "Video"
                                                                AttachmentType.AUDIO -> "Audio"
                                                                AttachmentType.DOCUMENT -> "Document"
                                                            }
                                                        )
                                                    }
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                } ?: Text(
                                    text = "Start a message...",
                                    style = LocalTextStyle.current.copy(
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        message?.sentAt?.let {
                            Text(
                                text = it.info().run {
                                    if (isToday) localDateTime.time.toStringIgnoreSecond()
                                    else if (daysAgo == 1) "Yesterday"
                                    else localDateTime.date.toStringFormatted()
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Box {
                            Icon(
                                painter = painterResource(KonnektIcon.ellipsisVertical),
                                contentDescription = "more",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(
                                        indication = null,
                                        interactionSource = null
                                    ) {
                                        if (dropdownItems != null)
                                            expandDropdown = !expandDropdown
                                    }
                            )
                            dropdownItems?.let {
                                if (chat.type != ChatType.CHAT_ROOM) DropdownMenu(
                                    expanded = expandDropdown,
                                    onDismissRequest = {
                                        expandDropdown = false
                                    },
                                    content = {
                                        it {
                                            expandDropdown = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatCardsPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    KonnektTheme {
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chats(
                    latestChatMessages = data.latestChatMessages,
                    onClick = {},
                    sentByCurrentUser = { false },
                    unreadByCurrentUser = { false },
                    deletedByCurrentUser = { false },
                    dropdownItems = { dismiss, chat ->
                        DropdownItem(
                            text = "Clear Chat",
                            onClick = {
                                dismiss()
                            },
                            contentColor = Red,
                            iconId = KonnektIcon.mailCheck
                        )
                        DropdownItem(
                            text = "Delete Chat",
                            onClick = {
                                dismiss()
                            },
                            contentColor = Red,
                            iconId = KonnektIcon.eye
                        )
                    }
                )
            }
        }
    }
}