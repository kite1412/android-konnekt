package nrr.konnekt.feature.chatinvitations

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.component.ShadowedButton
import nrr.konnekt.core.designsystem.theme.BrightRed
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.ButtonDefaults
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.component.CubicLoading
import nrr.konnekt.core.ui.component.SimpleHeader
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.topRadialGradient

@Composable
internal fun ChatInvitationsScreen(modifier: Modifier = Modifier) {

}

@Composable
private fun ChatInvitationsScreen(
    chatInvitations: List<ChatInvitation>?,
    contentPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    onInvitationAction: (ChatInvitation, accept: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .topRadialGradient()
            .bottomRadialGradient()
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SimpleHeader(
                title = "Chat Invitations",
                onNavigateBack = onNavigateBack
            )
            if (!chatInvitations.isNullOrEmpty()) LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = chatInvitations,
                    key = { it.id }
                ) { invitation ->
                    ChatInvitationCard(
                        chatInvitation = invitation,
                        onAction = {
                            onInvitationAction(invitation, it)
                        }
                    )
                }
            }
        }

        if (chatInvitations.isNullOrEmpty()) CubicLoading(
            text = "Loading chat invitations",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ChatInvitationCard(
    chatInvitation: ChatInvitation,
    onAction: (accept: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val chat = chatInvitation.chat

        Row(
            modifier = Modifier.weight(0.8f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AvatarIcon(
                name = chat.name(),
                iconPath = chat.setting?.iconPath,
                diameter = 60.dp
            )
            Column {
                val descTextStyle = MaterialTheme.typography.bodySmall

                Text(
                    text = chat.name(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (chat.type == ChatType.GROUP) "Group Chat" else "Chat Room",
                    style = descTextStyle
                )
                Text(
                    text = "from: ${chatInvitation.inviter.username}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = descTextStyle.copy(
                        color = Gray
                    )
                )
            }
        }
        Row(
            modifier = Modifier.weight(0.4f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val iconModifier = Modifier.size(18.dp)

            ShadowedButton(
                onClick = { onAction(false) },
                style = ButtonDefaults.defaultShadowedStyle(
                    backgroundColor = BrightRed
                )
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.x),
                    contentDescription = "reject",
                    modifier = iconModifier
                )
            }
            ShadowedButton(
                onClick = { onAction(true) }
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.check),
                    contentDescription = "accept",
                    modifier = iconModifier
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun ChatInvitationsPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    KonnektTheme {
        Scaffold {
            ChatInvitationsScreen(
                chatInvitations = data.chatInvitations,
                contentPadding = PaddingValues(16.dp),
                onNavigateBack = {},
                onInvitationAction = { _, _ -> }
            )
        }
    }
}