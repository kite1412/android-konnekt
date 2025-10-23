package nrr.konnekt.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.component.ShadowedBox
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.ShadowedBoxDefaults

@Composable
fun CubicLoading(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colorScheme.primary,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Bold
    )
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(size / 4)
        ) {
            repeat(3) {
                val infiniteTransition = rememberInfiniteTransition()
                val offsetYDp by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -16f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 200,
                            delayMillis = 400,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(offsetMillis = 200 * it)
                    )
                )

                ShadowedBox(
                    modifier = Modifier
                        .size(size)
                        .offset {
                            IntOffset(x = 0, y = offsetYDp.toDp().roundToPx())
                        },
                    style = ShadowedBoxDefaults.defaultStyle(
                        shadowColor = Color.Black,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        space = size / 6
                    )
                ) {}
            }
        }
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Preview
@Composable
private fun CubicLoadingPreview() {
    KonnektTheme {
        CubicLoading(
            text = "Loading...",
            size = 16.dp
        )
    }
}