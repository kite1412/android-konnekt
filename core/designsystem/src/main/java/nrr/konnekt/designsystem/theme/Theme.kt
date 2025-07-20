package nrr.konnekt.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Navy
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