package nrr.konnekt.core.ui.component

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.ui.util.ProgressBarDefaults
import nrr.konnekt.core.ui.util.ProgressBarStyle

@Composable
fun ProgressBar(
    @FloatRange(0.0, 1.0)
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ProgressBarStyle = ProgressBarDefaults.defaultStyle(),
    onDraggingChange: ((Boolean) -> Unit)? = null
) {
    with(style) {
        BoxWithConstraints(
            modifier = modifier
        ) {
            var progressDp by remember {
                mutableStateOf(this.maxWidth * progress)
            }
            var isDragging by remember { mutableStateOf(false) }
            val animatedProgress by animateDpAsState(progressDp)

            LaunchedEffect(progress, isDragging) {
                if (!isDragging) {
                    progressDp = maxWidth * progress
                        .coerceIn(0f, 1f)
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(shape)
                    .background(trackColor)
                    .pointerInput(enabled) {
                        if (enabled) detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                isDragging = true
                                onDraggingChange?.invoke(true)
                                progressDp = maxWidth * (change.position.x / size.width)
                                    .coerceIn(0f, 1f)
                            },
                            onDragEnd = {
                                onProgressChange(progressDp.toPx() / size.width)
                                isDragging = false
                                onDraggingChange?.invoke(false)
                            }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(animatedProgress)
                        .clip(shape)
                        .background(color)
                )
            }
            if (withThumb) Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = animatedProgress.roundToPx(),
                            y = 0
                        )
                    }
                    .size(thumbSize)
                    .clip(thumbShape)
                    .background(thumbColor)
            )
        }
    }
}

@Preview
@Composable
private fun ProgressBarPreview() {
    var progress by remember { mutableFloatStateOf(0f) }

    KonnektTheme {
        ProgressBar(
            progress = progress,
            onProgressChange = { progress = it }
        )
    }
}