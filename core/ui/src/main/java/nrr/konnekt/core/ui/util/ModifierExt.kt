package nrr.konnekt.core.ui.util

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.topRadialGradient(
    color: Color = MaterialTheme.colorScheme.primary,
    side: Side = Side.LEFT
): Modifier {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val width = with(density) {
        config.screenWidthDp.dp.toPx()
    }
    val height = with(density) {
        config.screenHeightDp.dp.toPx()
    }

    return background(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = Offset(
                x = width * ((if (side == Side.LEFT) 1f else 4f) / 5f),
                y = height * (1f / 6f)
            )
        ),
        alpha = 0.4f
    )
}

@Composable
fun Modifier.bottomRadialGradient(
    color: Color = MaterialTheme.colorScheme.primary,
    side: Side = Side.RIGHT
): Modifier {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val width = with(density) {
        config.screenWidthDp.dp.toPx()
    }
    val height = with(density) {
        config.screenHeightDp.dp.toPx()
    }

    return background(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = Offset(
                x = width * ((if (side == Side.LEFT) 1f else 4f) / 5f),
                y = height * (5.5f / 6f)
            )
        ),
        alpha = 0.5f
    )
}