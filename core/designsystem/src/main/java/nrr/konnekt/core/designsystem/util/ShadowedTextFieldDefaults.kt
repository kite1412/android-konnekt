package nrr.konnekt.core.designsystem.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ShadowedTextFieldDefaults {
    @Composable
    fun defaultStyle(
        labelTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
        textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        shadowColor: Color = MaterialTheme.colorScheme.primary,
        backgroundColor: Color = MaterialTheme.colorScheme.background,
        contentPadding: PaddingValues = PaddingValues(
            horizontal = 24.dp,
            vertical = 16.dp
        ),
        space: Dp = 6.dp
    ) = ShadowedTextFieldStyle(
        labelTextStyle = labelTextStyle,
        textStyle = textStyle,
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        contentPadding = contentPadding,
        space = space
    )
}

class ShadowedTextFieldStyle(
    val labelTextStyle: TextStyle,
    val textStyle: TextStyle,
    val shadowColor: Color,
    val backgroundColor: Color,
    val contentPadding: PaddingValues,
    val space: Dp
)