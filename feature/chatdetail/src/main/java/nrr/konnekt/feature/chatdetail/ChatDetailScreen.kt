package nrr.konnekt.feature.chatdetail

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldStyle
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.ui.component.ChatHeader
import nrr.konnekt.core.ui.previewparameter.Conversation
import nrr.konnekt.core.ui.previewparameter.ConversationProvider
import nrr.konnekt.feature.chatdetail.util.UiEvent
import kotlin.time.ExperimentalTime

@Composable
internal fun ChatDetailScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ChatDetailScreen(
    chat: Chat,
    totalActiveParticipants: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ChatHeader(
            chatName = chat.setting?.name ?: chat.id,
            chatIconPath = chat.setting?.iconPath,
            chatType = chat.type,
            totalActiveParticipants = totalActiveParticipants,
            onNavigateBack = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ChatInfo(
                    desc = chat.setting?.description,
                    chatType = chat.type
                )
            }
        }
    }
}

@Composable
private fun ChatInfo(
    chatType: ChatType,
    desc: String?,
    modifier: Modifier = Modifier,
    invitationId: String? = null,
    canEditDesc: Boolean = true,
    onDescChange: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var descEdit by rememberSaveable(desc) { mutableStateOf(desc ?: "") }
        var editDesc by rememberSaveable { mutableStateOf(false) }
        val editEnabled = chatType == ChatType.GROUP && canEditDesc && editDesc
        val focusRequester = remember { FocusRequester() }

        AdjustedShadowedTextField(
            value = descEdit,
            onValueChange = {
                descEdit = it
            },
            label = if (chatType == ChatType.PERSONAL) "Bio" else "Group Description",
            enabled = editEnabled,
            placeholder = if (chatType == ChatType.PERSONAL) "No Bio" else "No Description",
            actions = if (canEditDesc) {
                {
                    LaunchedEffect(editDesc) {
                        Log.d("feat:detail", editDesc.toString())
                        if (!editDesc) {
                            descEdit = desc ?: ""
                            focusRequester.freeFocus()
                        } else focusRequester.requestFocus()
                    }
                    AnimatedContent(
                        targetState = editDesc
                    ) { state ->
                        val transition = updateTransition(state, "ui state transition")

                        val tint by transition.animateColor(
                            label = "tint color"
                        ) { s ->
                            if (!s) MaterialTheme.colorScheme.primary else Red
                        }

                        Icon(
                            painter = painterResource(if (!state) KonnektIcon.pencil else KonnektIcon.x),
                            contentDescription = "edit",
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null
                            ) {
                                editDesc = !editDesc
                            },
                            tint = tint
                        )
                    }
                }
            } else null,
            style = TextFieldDefaults.defaultShadowedStyle(
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalContentColor.current,
                    fontStyle = if (descEdit.isEmpty()) FontStyle.Italic else FontStyle.Normal
                )
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (editEnabled && descEdit != desc)
                        onDescChange?.invoke(descEdit)
                    editDesc = false
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            focusRequester = focusRequester
        )
        invitationId?.let {
            AdjustedShadowedTextField(
                value = it,
                onValueChange = {},
                label = "Invitation ID",
                enabled = false
            )
        }
    }
}

@Composable
private fun AdjustedShadowedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    placeholder: String = "",
    style: ShadowedTextFieldStyle = TextFieldDefaults.defaultShadowedStyle(),
    actions: (@Composable () -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester? = null
) {
    ShadowedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        placeholder = placeholder,
        label = label,
        style = style.copy(
            labelTextStyle = MaterialTheme.typography.bodySmall.copy(
                color = Gray
            )
        ),
        actions = actions,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        focusRequester = focusRequester
    )
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ChatDetailScreenPreview(
    @PreviewParameter(ConversationProvider::class)
    conversation: Conversation
) {
    KonnektTheme {
        Scaffold {
            ChatDetailScreen(
                chat = conversation.chat.copy(
                    setting = conversation.chat.setting?.copy(
                        description = null
                    )
                ),
                totalActiveParticipants = 1,
                onNavigateBack = {},
                modifier = Modifier.padding(it)
            )
        }
    }
}