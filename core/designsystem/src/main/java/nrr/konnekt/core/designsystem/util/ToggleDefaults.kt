package nrr.konnekt.core.designsystem.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.Lime

object ToggleDefaults {
    @Composable
    fun defaultToggleStyle(
        checkedBackground: Color = MaterialTheme.colorScheme.primary,
        checkedThumbColor: Color = Lime,
        uncheckedBackground: Color = DarkGray,
        uncheckedThumbColor: Color = Gray,
        disabledBackgroundColor: Color = DarkGray.copy(alpha = 0.9f),
        disabledThumbColor: Color = DarkGray
    ) = ToggleStyle(
        checkedBackground = checkedBackground,
        checkedThumbColor = checkedThumbColor,
        uncheckedBackground = uncheckedBackground,
        uncheckedThumbColor = uncheckedThumbColor,
        disabledBackground = disabledBackgroundColor,
        disabledThumbColor = disabledThumbColor
    )
}

data class ToggleStyle(
    val checkedBackground: Color,
    val checkedThumbColor: Color,
    val uncheckedBackground: Color,
    val uncheckedThumbColor: Color,
    val disabledBackground: Color,
    val disabledThumbColor: Color
)