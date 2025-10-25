package nrr.konnekt.core.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.ToggleDefaults
import nrr.konnekt.core.designsystem.util.ToggleStyle

@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    square: Dp = 32.dp,
    enabled: Boolean = true,
    style: ToggleStyle = ToggleDefaults.defaultToggleStyle()
) {
    val padding = square / 6
    val transition = updateTransition(
        targetState = checked,
        label = "toggle transition"
    )
    val background by transition.animateColor {
        if (enabled)
            if (it) style.checkedBackground
            else style.uncheckedBackground
        else style.disabledBackground
    }
    val thumbColor by transition.animateColor {
        if (enabled)
            if (it) style.checkedThumbColor
            else style.uncheckedThumbColor
        else style.disabledThumbColor
    }
    val density = LocalDensity.current
    val offsetX by transition.animateFloat {
        with(density) {
            if (it) square.toPx() else 0f
        }
    }

    Canvas(
        modifier = modifier
            .size(
                height = square + padding * 2,
                width = square * 2 + padding * 2
            )
            .background(background)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = null
            ) {
                onCheckedChange(!checked)
            }
            .padding(padding)
    ) {
        drawRect(
            color = thumbColor,
            topLeft = Offset(
                x = offsetX,
                y = 0f
            ),
            size = size.copy(
                width = square.toPx()
            )
        )
    }
}

@Preview
@Composable
private fun TogglePreview() {
    var checked by remember { mutableStateOf(true) }

    KonnektTheme {
        Toggle(
            checked = checked,
            onCheckedChange = { checked = it },
            enabled = false
        )
    }
}