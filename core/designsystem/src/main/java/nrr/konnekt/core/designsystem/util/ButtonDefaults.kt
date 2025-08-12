package nrr.konnekt.core.designsystem.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

typealias ShadowedButtonStyle = ShadowedBoxStyle

object ButtonDefaults {
    @Composable
    fun defaultShadowedStyle(
        shadowColor: Color = MaterialTheme.colorScheme.onPrimary,
        backgroundColor: Color = MaterialTheme.colorScheme.primary,
        space: Dp = 4.dp,
        contentPadding: PaddingValues = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) = ShadowedButtonStyle(
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        space = space,
        contentPadding = contentPadding
    )
}