package nrr.konnekt.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.model.User

@Composable
fun MemberInvite(
    userContacts: List<User>?,
    selectedContacts: List<User>,
    onSelectContact: (User, selected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = userContacts,
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) { userContacts ->
        if (userContacts != null) {
            if (userContacts.isNotEmpty()) Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Invite Your Friends",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            max = 250.dp
                        )
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    items(
                        items = userContacts,
                        key = { it.id }
                    ) { user ->
                        val onClick: () -> Unit = {
                            if (selectedContacts.contains(user))
                                onSelectContact(user, false)
                            else onSelectContact(user, true)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = onClick
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            UserCard(
                                user = user,
                                onClick = { onClick() },
                                enabled = true,
                                modifier = Modifier.weight(0.9f)
                            )
                            RadioButton(
                                selected = selectedContacts.contains(user),
                                onClick = onClick
                            )
                        }
                    }
                }
            } else Text(
                text = "You don't have any contacts",
                style = LocalTextStyle.current.copy(
                    color = Gray,
                    fontStyle = FontStyle.Italic
                )
            )
        } else CubicLoading("Loading contacts")
    }
}