package nrr.konnekt.core.ui.util

import androidx.compose.ui.graphics.Color
import nrr.konnekt.core.designsystem.theme.Blue
import nrr.konnekt.core.designsystem.theme.Cyan
import nrr.konnekt.core.designsystem.theme.Purple
import nrr.konnekt.core.designsystem.theme.Red

fun Char?.getLetterColor(): Color {
    val colors = listOf(
        Purple,
        Red,
        Blue,
        Cyan
    )
    if (this == null) return colors.first()
    val uppercase = uppercaseChar()
    if (uppercase !in 'A'..'Z') {
        return colors.first()
    }
    val index = uppercase - 'A'

    return colors[index % colors.size]
}