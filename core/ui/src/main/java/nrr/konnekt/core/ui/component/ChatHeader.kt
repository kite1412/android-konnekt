package nrr.konnekt.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.ui.util.ActiveStatus
import kotlin.time.Instant

@Composable
fun ChatHeader(
    chatName: String,
    chatIconPath: String?,
    chatType: ChatType,
    totalActiveParticipants: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    peerLastActive: Instant? = null,
    onClick: (() -> Unit)? = null,
    iconSize: Dp = 32.dp,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    val name = @Composable { style: TextStyle ->
        Text(
            text = chatName,
            style = style.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (chatType != ChatType.CHAT_ROOM)
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val primary = MaterialTheme.colorScheme.primary
            val ripple = remember {
                ripple(
                    color = { primary },
                    bounded = false
                )
            }

            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.chevronLeft),
                    contentDescription = "back",
                    modifier = Modifier.size(iconSize),
                    tint = iconTint
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (onClick != null) Modifier.clickable(
                            indication = ripple,
                            interactionSource = null,
                            onClick = onClick
                        )
                        else Modifier
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarIcon(
                    name = chatName,
                    iconPath = chatIconPath,
                    diameter = 60.dp
                )
                Column {
                    name(MaterialTheme.typography.bodyLarge)
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodySmall
                    ) {
                        with(ActiveStatus) {
                            when (chatType) {
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
    else Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            name(MaterialTheme.typography.titleSmall)
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