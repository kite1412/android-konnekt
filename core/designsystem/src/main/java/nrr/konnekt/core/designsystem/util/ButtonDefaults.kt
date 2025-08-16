package nrr.konnekt.core.designsystem.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.Gray

object ButtonDefaults {
    @Composable
    fun defaultShadowedStyle(
        shadowColor: Color = MaterialTheme.colorScheme.onPrimary,
        backgroundColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = shadowColor,
        disabledShadowColor: Color = Gray,
        disabledBackgroundColor: Color = backgroundColor.copy(alpha = 0.7f),
        disabledContentColor: Color = disabledShadowColor,
        space: Dp = 4.dp,
        contentPadding: PaddingValues = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) = ShadowedButtonStyle(
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        disabledShadowColor = disabledShadowColor,
        disabledBackgroundColor = disabledBackgroundColor,
        disabledContentColor = disabledContentColor,
        space = space,
        contentPadding = contentPadding
    )
}

data class ShadowedButtonStyle(
    val shadowColor: Color,
    val backgroundColor: Color,
    val contentColor: Color,
    val disabledShadowColor: Color,
    val disabledBackgroundColor: Color,
    val disabledContentColor: Color,
    val space: Dp,
    val contentPadding: PaddingValues
)

internal fun ShadowedButtonStyle.toShadowedBoxStyle(
    enabled: Boolean,
    shadowColor: Color = this.shadowColor,
    backgroundColor: Color = this.backgroundColor,
    contentColor: Color = this.contentColor,
    space: Dp = this.space,
    contentPadding: PaddingValues = this.contentPadding
) = ShadowedBoxStyle(
    shadowColor = if (enabled) shadowColor else disabledShadowColor,
    backgroundColor = if (enabled) backgroundColor else disabledBackgroundColor,
    contentColor = if (enabled) contentColor else disabledContentColor,
    borderColor = shadowColor,
    borderWidth = 2.dp,
    space = space,
    contentPadding = contentPadding
)