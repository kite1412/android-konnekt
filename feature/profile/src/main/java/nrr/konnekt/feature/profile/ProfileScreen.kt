package nrr.konnekt.feature.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.toDateString
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider

@Composable
internal fun ProfileScreen(
    navigateBack: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    user?.let { user ->
        ProfileScreen(
            user = user,
            contentPadding = contentPadding,
            onNavigateBack = navigateBack,
            modifier = modifier
        )
    }
}

@Composable
internal fun ProfileScreen(
    user: User,
    contentPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Header(
            onNavigateBack = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )

        UserInfo(
            user = user
        )
    }
}

@Composable
private fun Header(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(KonnektIcon.chevronLeft),
                    contentDescription = "back",
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun UserInfo(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleStyle = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        )

        AvatarIcon(
            name = user.username,
            iconPath = user.imagePath,
            diameter = (titleStyle.fontSize.value * 4).dp
        )
        Text(
            text = user.username,
            style = titleStyle,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Bio",
                modifier = Modifier.padding(start = 4.dp),
                color = Gray
            )
            Text(
                text = user.bio ?: "You don't have a bio.",
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(size = 8.dp)
                    )
                    .padding(16.dp),
                style = LocalTextStyle.current.copy(
                    color = if (user.bio == null) DarkGray else LocalContentColor.current,
                    fontStyle = if (user.bio == null) FontStyle.Italic else FontStyle.Normal
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HorizontalDivider(
                thickness = 2.dp,
                color = DarkGray
            )
            Detail(
                label = "Email",
                value = user.email,
                iconId = KonnektIcon.mail
            )
            Detail(
                label = "Joined At",
                value = user.createdAt.toDateString(
                    dateFormat = "d MMMM yyyy"
                ),
                iconId = KonnektIcon.calendar
            )
        }
    }
}

@Composable
private fun Detail(
    label: String,
    value: String,
    iconId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides Gray) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = label,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(PreviewParameterDataProvider::class)
    data: PreviewParameterData
) {
    KonnektTheme {
        Scaffold {
            ProfileScreen(
                user = data.user.copy(
                    username = "a very long long long long long long long username"
                ),
                contentPadding = it,
                onNavigateBack = {}
            )
        }
    }
}