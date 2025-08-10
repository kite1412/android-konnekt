package nrr.konnekt.core.designsystem.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.Gray

object TextFieldDefaults {
    private val defaultTextStyle: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium.copy(
            color = LocalContentColor.current
        )

    private val defaultBackgroundColor: Color
        @Composable get() = MaterialTheme.colorScheme.background

    private val defaultPlaceholderColor = Gray
    private val defaultContentPadding = PaddingValues(
        horizontal = 24.dp,
        vertical = 16.dp
    )

    @Composable
    fun defaultShadowedStyle(
        labelTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
        textStyle: TextStyle = defaultTextStyle,
        shadowColor: Color = MaterialTheme.colorScheme.primary,
        backgroundColor: Color = defaultBackgroundColor,
        placeholderColor: Color = defaultPlaceholderColor,
        contentPadding: PaddingValues = defaultContentPadding,
        space: Dp = 6.dp
    ) = ShadowedTextFieldStyle(
        labelTextStyle = labelTextStyle,
        textStyle = textStyle,
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        placeholderColor = placeholderColor,
        contentPadding = contentPadding,
        space = space
    )

    @Composable
    fun defaultOutlinedStyle(
        outlineColor: Color = MaterialTheme.colorScheme.primary,
        textStyle: TextStyle = defaultTextStyle,
        backgroundColor: Color = defaultBackgroundColor,
        contentPadding: PaddingValues = defaultContentPadding
    ) = OutlinedTextFieldStyle(
        outlineColor = outlineColor,
        textStyle = textStyle,
        backgroundColor = backgroundColor,
        placeholderColor = defaultPlaceholderColor,
        contentPadding = contentPadding
    )
}

class ShadowedTextFieldStyle(
    val labelTextStyle: TextStyle,
    val textStyle: TextStyle,
    val shadowColor: Color,
    val backgroundColor: Color,
    val placeholderColor: Color,
    val contentPadding: PaddingValues,
    val space: Dp
)

class OutlinedTextFieldStyle(
    val outlineColor: Color,
    val textStyle: TextStyle,
    val backgroundColor: Color,
    val placeholderColor: Color,
    val contentPadding: PaddingValues,
)