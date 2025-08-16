package nrr.konnekt.core.designsystem.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ShadowedBoxDefaults {
    /**
     * @param space - space between shadow and content
     */
    @Composable
    fun defaultStyle(
        shadowColor: Color = MaterialTheme.colorScheme.primary,
        backgroundColor: Color = MaterialTheme.colorScheme.background,
        contentColor: Color = shadowColor,
        borderColor: Color = shadowColor,
        borderWidth: Dp = 2.dp,
        space: Dp = 4.dp,
        contentPadding: PaddingValues = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) = ShadowedBoxStyle(
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        borderColor = borderColor,
        borderWidth = borderWidth,
        space = space,
        contentPadding = contentPadding
    )
}

data class ShadowedBoxStyle(
    val shadowColor: Color,
    val backgroundColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val borderWidth: Dp,
    val space: Dp,
    val contentPadding: PaddingValues,
)