/**
 * Message seen indicators as extensions to [MessageBubble]
 */

package nrr.konnekt.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.model.User
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider

object MessageSeenIndicator {
    @Composable
    fun PersonalSeenIndicator(modifier: Modifier = Modifier) {
        Text(
            text = "Seen",
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        )
    }

    @Composable
    fun GroupSeenIndicator(
        seenBy: List<User>,
        modifier: Modifier = Modifier,
        overlap: Dp = 8.dp,
        maxShown: Int = 5,
        avatarDiameter: Dp = 25.dp
    ) {
        if (seenBy.isNotEmpty()) Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            seenBy.take(maxShown).forEachIndexed { i, u ->
                AvatarIcon(
                    name = u.username,
                    modifier = Modifier.offset(
                        x = -i * overlap
                    ),
                    iconPath = u.imagePath,
                    diameter = avatarDiameter
                )
            }
            if (seenBy.size > maxShown) Icon(
                painter = painterResource(KonnektIcon.ellipsis),
                contentDescription = "more",
                modifier = Modifier
                    .size(avatarDiameter / 2)
                    .offset(x = (-maxShown + 1) * (overlap / 1.2f)),
                tint = DarkGray
            )
        }
    }
}

@Preview
@Composable
private fun GroupSeenIndicatorPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    with(MessageSeenIndicator) {
        GroupSeenIndicator(
            seenBy = data.latestChatMessages.mapNotNull {
                it.message?.sender
            },
            maxShown = 5,
            avatarDiameter = 30.dp
        )
    }
}