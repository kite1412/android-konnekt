package nrr.konnekt.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.delay
import nrr.konnekt.core.designsystem.component.ShadowedBox
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Lime
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedBoxDefaults
import nrr.konnekt.core.designsystem.util.ShadowedBoxStyle
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.getAudioDurationMs
import nrr.konnekt.core.ui.util.getVideoThumbnail
import nrr.konnekt.core.ui.util.msToString
import nrr.konnekt.core.ui.util.rememberResolvedFile
import nrr.konnekt.core.ui.util.toTimeString
import kotlin.time.Instant

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
    Column(
        modifier = modifier
            .then(
                if (!withTail)
                    if (sentByCurrentUser) Modifier.padding(end = tailSize)
                    else Modifier.padding(start = tailSize)
                else Modifier
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = if (sentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        if (message.attachments.isNotEmpty())
            MessageAttachments(
                attachments = message.attachments,
                messageSentAt = message.sentAt,
                maxWidth = maxContentWidth,
                borderColor = shadowedBoxStyle.borderColor,
                modifier = Modifier
                    .padding(
                        start = if (!sentByCurrentUser && withTail) tailSize else 0.dp,
                        end = if (sentByCurrentUser && withTail) tailSize else 0.dp
                    )
            )
        if (message.content.isNotBlank()) Row {
            if (withTail && !sentByCurrentUser) Tail(
                size = tailSize,
                color = tailColor,
                reverse = true
            )
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
            if (withTail && sentByCurrentUser) Tail(
                size = tailSize,
                color = tailColor,
                reverse = false
            )
        }
        if (sentByCurrentUser && seenContent != null) Box(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = tailSize / 2)
        ) {
            seenContent(MessageSeenIndicator)
        }
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
    withAvatar: Boolean = true,
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
        if (withAvatar) Row(
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
            if (withTail && message.attachments.isEmpty()) Canvas(
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

@Composable
private fun ColumnScope.MessageAttachments(
    attachments: List<Attachment>,
    messageSentAt: Instant,
    maxWidth: Dp,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    attachments.firstOrNull()?.let { a ->
        val attachmentContent by rememberResolvedFile(a.path)
        val maxWidth = min(maxWidth, 400.dp)

        if (attachmentContent != null) {
            val shape = RoundedCornerShape(8.dp)

            Box(
                modifier = modifier
                    .sizeIn(
                        maxWidth = maxWidth,
                        maxHeight = maxWidth
                    )
                    .clip(shape)
            ) {
                val applyBorder: Modifier.() -> Modifier = {
                    border(
                        width = 1.dp,
                        color = borderColor,
                        shape = shape
                    )
                }

                when (a.type) {
                    AttachmentType.IMAGE -> with(attachmentContent!!.asImageBitmap()) {
                        val ratio = this.width.toFloat() / this.height

                        Image(
                            bitmap = this,
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(ratio)
                                .applyBorder(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    AttachmentType.VIDEO -> attachmentContent?.let { c ->
                        val thumbnail = LocalContext.current.getVideoThumbnail(c)

                        thumbnail?.let { t ->
                            VideoPreview(
                                thumbnail = t,
                                modifier = Modifier.applyBorder()
                            )
                        }
                    }
                    AttachmentType.AUDIO -> attachmentContent?.let { c ->
                        val duration = LocalContext.current.getAudioDurationMs(c)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            AudioAttachment(
                                play = false,
                                onPlayChange = {},
                                progressMs = 0L,
                                durationMs = duration,
                                background = borderColor,
                                modifier = Modifier.clip(shape)
                            )
                            AttachmentsInfo(
                                attachmentsSize = attachments.size,
                                messageSentAt = messageSentAt
                            )
                        }
                    }
                    AttachmentType.DOCUMENT -> Unit
                }
                if (a.type == AttachmentType.VIDEO || a.type == AttachmentType.IMAGE) AttachmentsInfo(
                    attachmentsSize = attachments.size,
                    messageSentAt = messageSentAt,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset((-8).dp, (-8).dp)
                        .height(IntrinsicSize.Max)
                )
            }
        } else {
            var loadingText by remember { mutableStateOf("Loading media") }

            LaunchedEffect(Unit) {
                var i = 0
                while (true) {
                    if (i in 0..2) {
                        loadingText += "."
                        i++
                    } else {
                        loadingText = "Loading media"
                        i = 0
                    }
                    delay(500)
                }
            }
            Text(
                text = loadingText,
                modifier = modifier.align(Alignment.Start),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

@Composable
private fun AttachmentsInfo(
    attachmentsSize: Int,
    messageSentAt: Instant,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold
        )
    ) {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .background(Color.Black.copy(0.7f))
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (attachmentsSize > 1) {
                Text(
                    text = "${attachmentsSize - 1}+",
                    style = LocalTextStyle.current.copy(
                        fontStyle = FontStyle.Italic
                    )
                )
                VerticalDivider(
                    color = LocalContentColor.current,
                    modifier = Modifier.fillMaxHeight()
                )
            }
            Text(
                text = messageSentAt.toTimeString()
            )
        }
    }
}

@Composable
private fun VideoPreview(
    thumbnail: ImageBitmap,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Image(
            bitmap = thumbnail,
            contentDescription = "video",
            modifier = Modifier
                .blur(4.dp),
            contentScale = ContentScale.FillWidth
        )
        Icon(
            painter = painterResource(KonnektIcon.play),
            contentDescription = "play video",
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(16.dp)
                .align(Alignment.Center),
            tint = Color.White
        )
    }
}

@Composable
private fun AudioAttachment(
    play: Boolean,
    onPlayChange: (Boolean) -> Unit,
    progressMs: Long,
    durationMs: Long,
    background: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(play) {
            Icon(
                painter = painterResource(
                    if (it) KonnektIcon.pause
                    else KonnektIcon.play
                ),
                contentDescription = "play audio",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        onPlayChange(!it)
                    },
                tint = Color.Black
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .width(IntrinsicSize.Max)
                // arbitrary value
                .padding(top = 4.dp)
        ) {
            LinearProgressIndicator(
                progress = { progressMs.toFloat() / durationMs },
                modifier = Modifier.height(8.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.5f)
            ) {}
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(
                        color = Color.Black
                    )
                ) {
                    Text(msToString(progressMs))
                    Text(msToString(durationMs))
                }
            }
        }
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
        sender = User(
            id = "u1",
            username = "Mock",
            email = "mock@mock.com",
            createdAt = now()
        ),
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
                    it.message
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
                    it.message
                },
                sentByCurrentUser = false
            )
            MessageBubble(
                sender = data.user,
                message = data.latestChatMessages.firstNotNullOf {
                    it.message
                },
                seenContent = {
                    GroupSeenIndicator(
                        seenBy = data.latestChatMessages.mapNotNull {
                            it.message?.sender
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