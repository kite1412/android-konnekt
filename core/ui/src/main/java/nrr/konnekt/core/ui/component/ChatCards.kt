package nrr.konnekt.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.component.ShadowedButton
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.util.info
import nrr.konnekt.core.model.util.toStringFormatted
import nrr.konnekt.core.model.util.toStringIgnoreSecond
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.getLetterColor
import nrr.konnekt.core.ui.util.rememberResolvedImage

fun LazyListScope.chats(
    latestChatMessages: List<LatestChatMessage>,
    onClick: (LatestChatMessage) -> Unit,
    dropdownItems: (@Composable ColumnScope.(dismiss: () -> Unit, LatestChatMessage) -> Unit)? = null
) {
    items(
        count = latestChatMessages.size,
        key = { it }
    ) {
        with(latestChatMessages[it]) {
            ChatCard(
                latestChatMessage = this,
                onClick = onClick,
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
    modifier: Modifier = Modifier,
    dropdownItems: (@Composable ColumnScope.(dismiss: () -> Unit) -> Unit)? = null
) {
    var expandDropdown by remember { mutableStateOf(false) }
    val icon by rememberResolvedImage(latestChatMessage.chat.setting?.iconPath)

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onPrimary
    ) {
        ShadowedButton(
            onClick = { onClick(latestChatMessage) },
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 10.dp,
                vertical = 16.dp
            ),
            space = 6.dp,
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            icon?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = "chat icon",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: with(chat.setting?.name?.firstOrNull()) char@{
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(getLetterColor()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    this@char?.let {
                                        Text(
                                            text = it.toString(),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = chat.setting?.name ?: chat.id,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = buildAnnotatedString {
                                    messageDetail?.let {
                                        append("${it.sender.username}: ")
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.White
                                            )
                                        ) {
                                            append(it.message.content)
                                        }
                                    } ?: withStyle(
                                        style = SpanStyle(fontStyle = FontStyle.Italic)
                                    ) {
                                        append("Start a message...")
                                    }
                                },
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        messageDetail?.message?.sentAt?.let {
                            Text(
                                text = it.info().run {
                                    if (isToday) localDateTime.time.toStringIgnoreSecond()
                                    else if (daysAgo == 1) "Yesterday"
                                    else localDateTime.toStringFormatted()
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