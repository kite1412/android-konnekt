package nrr.konnekt.feature.conversation.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import nrr.konnekt.core.designsystem.theme.Red
import nrr.konnekt.core.designsystem.util.KonnektIcon

enum class SelectedMessageAction(val iconId: Int, val iconColor: Color) {
    DELETE_MESSAGE(iconId = KonnektIcon.trash, iconColor = Red),
    EDIT_MESSAGE(iconId = KonnektIcon.pencil, iconColor = Color.Transparent)
}