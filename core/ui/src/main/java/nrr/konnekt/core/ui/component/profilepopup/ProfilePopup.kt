package nrr.konnekt.core.ui.component.profilepopup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import nrr.konnekt.core.designsystem.component.ShadowedBox
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.DarkNavy
import nrr.konnekt.core.designsystem.theme.GreenPrimaryDarken
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedBoxDefaults
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider

@Composable
fun ProfilePopup(
    data: ProfilePopupData,
    onDismissRequest: () -> Unit,
    onMessageClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    additionalActions: (
    @Composable FlowRowScope.(@Composable (
        iconId: Int,
        text: String,
        contentColor: Color?,
        onClick: () -> Unit
    ) -> Unit) -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ShadowedBox(
            modifier = modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            style = ShadowedBoxDefaults.defaultStyle(
                contentColor = MaterialTheme.colorScheme.onBackground,
                backgroundColor = DarkNavy,
                shadowColor = GreenPrimaryDarken,
                borderWidth = 4.dp,
                space = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val chatName = data.name

                    AvatarIcon(
                        name = chatName,
                        iconPath = data.iconPath,
                        diameter = 120.dp
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = chatName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        data.description?.let { description ->
                            Text(
                                text = description,
                                color = DarkGray,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    Action(
                        iconId = KonnektIcon.messageCircle,
                        text = "Message",
                        onClick = onMessageClick
                    )
                    Action(
                        iconId = KonnektIcon.info,
                        text = "Info",
                        onClick = onInfoClick
                    )
                    additionalActions?.invoke(this) { iconId, text, color, onClick ->
                        Action(
                            iconId = iconId,
                            text = text,
                            onClick = onClick,
                            contentColor = color ?: MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.x),
                    contentDescription = "close",
                    tint = Red
                )
            }
        }
    }
}

@Composable
private fun Action(
    iconId: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor
    ) {
        val shape = RoundedCornerShape(8.dp)

        Column(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = contentColor,
                    shape = shape
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = shape
                )
                .size(90.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onClick
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = text,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = LocalTextStyle.current.fontSize
            )
        }
    }
}

@Preview
@Composable
private fun ProfilePopupPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    KonnektTheme {
        ProfilePopup(
            data = data.latestChatMessages
                .first { it.chat.type == ChatType.GROUP }
                .chat
                .toChatPopupData(),
            onDismissRequest = {},
            onMessageClick = {},
            onInfoClick = {}
        ) { action ->
            action(KonnektIcon.messageCircleX, "Clear Chat", null) {}
        }
    }
}