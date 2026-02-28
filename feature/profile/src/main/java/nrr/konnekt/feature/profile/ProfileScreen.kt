package nrr.konnekt.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nrr.konnekt.core.designsystem.component.OutlinedTextField
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.designsystem.util.TextFieldErrorIndicator
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.toDateString
import nrr.konnekt.core.network.upload.util.ValidationResult
import nrr.konnekt.core.ui.component.AvatarIcon
import nrr.konnekt.core.ui.compositionlocal.LocalFileUploadValidator
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.previewparameter.PreviewParameterData
import nrr.konnekt.core.ui.previewparameter.PreviewParameterDataProvider
import nrr.konnekt.core.ui.util.uriToByteArray

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
            onUserChange = {},
            onNavigateBack = navigateBack,
            modifier = modifier
        )
    }
}

@Composable
internal fun ProfileScreen(
    user: User,
    contentPadding: PaddingValues,
    onUserChange: (UserEdit) -> Unit,
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
            user = user,
            onUserChange = onUserChange
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
    onUserChange: (UserEdit) -> Unit,
    modifier: Modifier = Modifier
) {
    val userEdit by remember(user) {
        derivedStateOf {
            UserEdit(
                username = user.username,
                profileImage = null,
                bio = user.bio
            )
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleStyle = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        )

        ProfileImage(
            username = user.username,
            imagePath = user.imagePath,
            imageDiameter = (titleStyle.fontSize.value * 4).dp,
            onProfileImageChange = { onUserChange(userEdit.copy(profileImage = it)) }
        )
        Username(
            username = user.username,
            onUsernameChange = { onUserChange(userEdit.copy(username = it)) },
            textStyle = titleStyle
        )
        Bio(
            bio = user.bio,
            onBioChange = { onUserChange(userEdit.copy(bio = it)) }
        )
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
private fun ProfileImage(
    username: String,
    imagePath: String?,
    imageDiameter: Dp,
    onProfileImageChange: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fileUploadValidator = LocalFileUploadValidator.current
    val snackbarHostState = LocalSnackbarHostState.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { uri ->
            when (val result = fileUploadValidator(uri)) {
                is ValidationResult.Valid -> onProfileImageChange(context.uriToByteArray(uri))
                is ValidationResult.Invalid -> snackbarHostState.showSnackbar(
                    message = fileUploadValidator.getViolationReasonMessage(result.exception.reason)
                )
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = null,
                indication = null
            ) {
                launcher.launch("image/*")
            }
    ) {
        AvatarIcon(
            name = username,
            iconPath = imagePath,
            diameter = imageDiameter
        )
        Icon(
            painter = painterResource(KonnektIcon.repeat),
            contentDescription = "edit profile image",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun Username(
    username: String,
    textStyle: TextStyle,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by retain {
        mutableStateOf(false)
    }
    var editableUsername by retain(username) {
        mutableStateOf(username)
    }

    AnimatedContent(
        targetState = isEditing,
        modifier = modifier
    ) {
        if (!it) Box {
            Text(
                text = username,
                modifier = Modifier.padding(horizontal = 24.dp),
                style = textStyle,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painter = painterResource(KonnektIcon.pencil),
                contentDescription = "edit",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        isEditing = true
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        } else Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Change Username",
                modifier = Modifier.align(Alignment.Start)
            )
            OutlinedTextField(
                value = editableUsername,
                onValueChange = { name -> editableUsername = name },
                placeholder = "Enter your new username.",
                singleLine = true,
                errorIndicators = listOf(
                    TextFieldErrorIndicator(
                        error = editableUsername.isEmpty(),
                        message = "Username can't be empty."
                    )
                ),
                style = TextFieldDefaults.defaultOutlinedStyle(
                    contentPadding = PaddingValues(12.dp)
                )
            )
            Row {
                IconButton(
                    onClick = {
                        isEditing = false
                        editableUsername = username
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Red
                    )
                ) {
                    Icon(
                        painter = painterResource(KonnektIcon.x),
                        contentDescription = "cancel"
                    )
                }
                IconButton(
                    onClick = {
                        onUsernameChange(editableUsername)
                        isEditing = false
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        painter = painterResource(KonnektIcon.check),
                        contentDescription = "save"
                    )
                }
            }
        }
    }
}

@Composable
private fun Bio(
    bio: String?,
    onBioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isEditing by retain {
        mutableStateOf(false)
    }
    var editableBio by retain(bio) {
        mutableStateOf(bio ?: "")
    }
    val labelColor by animateColorAsState(
        targetValue = if (isEditing) LocalContentColor.current else Gray
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Bio",
            modifier = Modifier.padding(start = 4.dp),
            color = labelColor
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            BasicTextField(
                value = editableBio,
                onValueChange = { editableBio = it },
                modifier = Modifier.focusRequester(focusRequester),
                readOnly = !isEditing,
                decorationBox = {
                    if (editableBio.isEmpty() && !isEditing) Text(
                        text = "You don't have a bio.",
                        style = LocalTextStyle.current.copy(
                            color = DarkGray,
                            fontStyle = FontStyle.Italic
                        )
                    )
                    it()
                },
                textStyle = LocalTextStyle.current.copy(
                    color = LocalContentColor.current
                ),
                cursorBrush = SolidColor(LocalContentColor.current)
            )
            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                val iconModifier = Modifier.size(LocalTextStyle.current.fontSize.value.dp)

                AnimatedVisibility(isEditing) {
                    TextButton(
                        onClick = {
                            isEditing = false
                            editableBio = bio ?: ""
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Red
                        )
                    ) {
                        Text("Cancel")
                        Icon(
                            painter = painterResource(KonnektIcon.x),
                            contentDescription = "cancel",
                            modifier = iconModifier
                        )
                    }
                }
                AnimatedContent(isEditing) {
                    TextButton(
                        onClick = {
                            if (it) {
                                onBioChange(editableBio)
                                isEditing = false
                            }
                            else {
                                isEditing = true
                                keyboardController?.show()
                                focusRequester.requestFocus()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (it) "Save" else "Edit")
                        Icon(
                            painter = painterResource(
                                if (it) KonnektIcon.check else KonnektIcon.pencil
                            ),
                            contentDescription = if (it) "save" else "edit",
                            modifier = iconModifier
                        )
                    }
                }
            }
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
                    username = "kite1412",
                    bio = ""
                ),
                contentPadding = it,
                onUserChange = {},
                onNavigateBack = {}
            )
        }
    }
}