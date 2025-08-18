package nrr.konnekt.core.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.component.ShadowedBox
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Lime
import nrr.konnekt.core.designsystem.util.ShadowedBoxDefaults
import nrr.konnekt.core.designsystem.util.ShadowedBoxStyle
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.toTimeString

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier,
    sentByCurrentUser: Boolean = true,
    deletedByCurrentUser: Boolean = false,
    shadowedBoxStyle: ShadowedBoxStyle = ShadowedBoxDefaults.defaultStyle(
        shadowColor = Color.Black,
        backgroundColor = Lime,
        borderColor = if (sentByCurrentUser) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary,
        borderWidth = 4.dp,
        space = 8.dp,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 8.dp
        )
    ),
    withTail: Boolean = true,
    tailSize: Dp = 10.dp,
    tailColor: Color = shadowedBoxStyle.borderColor,
    maxContentWidth: Dp = 350.dp,
    seenContent: (@Composable MessageSeenIndicator.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .then(
                if (!withTail) {
                    if (sentByCurrentUser) Modifier.padding(end = tailSize)
                    else Modifier.padding(start = tailSize)
                } else Modifier
            ),
    ) {
        if (withTail && !sentByCurrentUser) Tail(
            size = tailSize,
            color = tailColor,
            reverse = true
        )
        Column(
            modifier = Modifier.sizeIn(maxWidth = maxContentWidth),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ShadowedBox(
                reverse = !sentByCurrentUser,
                style = shadowedBoxStyle
            ) {
                with(message) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (isHidden) "Message has been deleted" else if (deletedByCurrentUser)
                                "You deleted this message" else content,
                            style = LocalTextStyle.current.copy(
                                color = if (isHidden || deletedByCurrentUser) DarkGray else shadowedBoxStyle.contentColor,
                                fontStyle = if (isHidden || deletedByCurrentUser) FontStyle.Italic else FontStyle.Normal
                            )
                        )
                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(
                                color = DarkGray
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (sentAt != editedAt) Text(
                                    text = "Edited",
                                    style = LocalTextStyle.current.copy(
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                                Text(sentAt.toTimeString())
                            }
                        }
                    }
                }
            }
            if (sentByCurrentUser && seenContent != null) seenContent(MessageSeenIndicator)
        }
        if (withTail && sentByCurrentUser) Tail(
            size = tailSize,
            color = tailColor,
            reverse = false
        )
    }
}

@Composable
fun MessageBubble(
    sender: User,
    message: Message,
    modifier: Modifier = Modifier,
    sentByCurrentUser: Boolean = true,
    deletedByCurrentUser: Boolean = false,
    shadowedBoxStyle: ShadowedBoxStyle = ShadowedBoxDefaults.defaultStyle(
        shadowColor = Color.Black,
        backgroundColor = Lime,
        borderColor = if (sentByCurrentUser) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary,
        borderWidth = 4.dp,
        space = 8.dp,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 8.dp
        )
    ),
    withTail: Boolean = true,
    tailSize: Dp = 10.dp,
    tailColor: Color = shadowedBoxStyle.borderColor,
    avatarDiameter: Dp = 40.dp,
    maxContentWidth: Dp = 350.dp,
    seenContent: (@Composable MessageSeenIndicator.() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = if (sentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val composables = listOf<@Composable () -> Unit>(
                {
                    AvatarIcon(
                        name = sender.username,
                        iconPath = sender.imagePath,
                        diameter = avatarDiameter
                    )
                },
                {
                    Text(
                        text = sender.username,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )

            composables.run {
                (if (sentByCurrentUser) asReversed() else this).forEach {
                    it()
                }
            }
        }
        Column(
            horizontalAlignment = if (sentByCurrentUser) Alignment.End else Alignment.Start
        ) {
            if (withTail) Canvas(
                modifier = with(avatarDiameter / 2) {
                    Modifier
                        .padding(
                            start = if (sentByCurrentUser) 0.dp else this,
                            end = if (sentByCurrentUser) this else 0.dp
                        )
                        .size(tailSize)
                }
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(
                        x = if (!sentByCurrentUser) 0f else size.width,
                        y = 0f
                    )
                    lineTo(size.width, size.height)
                    close()
                }

                drawPath(
                    path = path,
                    color = tailColor
                )
            }
            MessageBubble(
                message = message,
                modifier = Modifier
                    .padding(
                        start = if (sentByCurrentUser) 0.dp else avatarDiameter / 2 - tailSize,
                        end = if (sentByCurrentUser) tailSize else 0.dp
                    ),
                sentByCurrentUser = sentByCurrentUser,
                shadowedBoxStyle = shadowedBoxStyle,
                withTail = false,
                deletedByCurrentUser = deletedByCurrentUser,
                tailSize = tailSize,
                maxContentWidth = maxContentWidth,
                seenContent = seenContent
            )
        }
    }
}

@Composable
private fun Tail(
    size: Dp,
    reverse: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .width(size)
            .height(size * 2)
    ) {
        val size = this.size
        val path = Path().apply {
            moveTo(
                x = if (!reverse) 0f else size.width,
                y = if (!reverse) 0f else size.height
            )
            lineTo(
                x = size.width,
                y = 0f
            )
            lineTo(
                x = 0f,
                y = if (!reverse) size.height else 0f
            )
            close()
        }
        drawPath(
            path = path,
            color = color
        )
    }
}

@Preview
@Composable
private fun MessageBubblePreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    val message = Message(
        id = "m1",
        chatId = "c1",
        senderId = "u1",
        content = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit,
            sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 
            Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
            Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
            Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
        """.trimIndent(),
        sentAt = now(),
        editedAt = now(),
        isHidden = false
    )

    KonnektTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MessageBubble(
                message = data.latestChatMessages.firstNotNullOf {
                    it.messageDetail?.message
                }
            )
            MessageBubble(
                message = message,
                withTail = false,
                deletedByCurrentUser = true,
                seenContent = {
                    PersonalSeenIndicator()
                }
            )
            MessageBubble(
                sender = data.user,
                message = data.latestChatMessages.firstNotNullOf {
                    it.messageDetail?.message
                },
                sentByCurrentUser = false
            )
            MessageBubble(
                sender = data.user,
                message = data.latestChatMessages.firstNotNullOf {
                    it.messageDetail?.message
                },
                seenContent = {
                    GroupSeenIndicator(
                        seenBy = data.latestChatMessages.mapNotNull {
                            it.messageDetail?.sender
                        },
                        maxShown = 2
                    )
                }
            )
            MessageBubble(
                message = message,
                sentByCurrentUser = false,
                withTail = false
            )
        }
    }
}