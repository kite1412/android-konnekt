package nrr.konnekt.core.ui.util

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.GreenPrimaryDarken

object ProgressBarDefaults {
    @Composable
    fun defaultStyle(
        color: Color = MaterialTheme.colorScheme.primary,
        trackColor: Color = color.copy(alpha = 0.5f),
        thumbColor: Color = GreenPrimaryDarken,
        withThumb: Boolean = true,
        shape: Shape = CircleShape,
        thumbShape: Shape = CircleShape,
        thumbSize: Dp = 12.dp
    ) = ProgressBarStyle(
        color = color,
        trackColor = trackColor,
        thumbColor = thumbColor,
        withThumb = withThumb,
        shape = shape,
        thumbShape = thumbShape,
        thumbSize = thumbSize
    )
}

data class ProgressBarStyle(
    val color: Color,
    val trackColor: Color,
    val thumbColor: Color,
    val withThumb: Boolean,
    val shape: Shape,
    val thumbShape: Shape,
    val thumbSize: Dp
)