package nrr.konnekt.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = Cyan,
    tertiary = Pink80,
    background = Navy,
    onPrimary = Color.Black,
    outline = Lime,
    error = Red
)

@Composable
fun KonnektTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography
    ) {
        ProvideTextStyle(Typography.bodyMedium, content)
    }
}