package nrr.konnekt.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nrr.konnekt.core.designsystem.KonnektDefaults

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColors: ButtonColors = KonnektDefaults.buttonColors(),
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = buttonColors,
        enabled = enabled,
        content = content
    )
}