package nrr.konnekt.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.ShadowedBoxDefaults
import nrr.konnekt.core.designsystem.util.ShadowedBoxStyle

@Composable
fun ShadowedBox(
    modifier: Modifier = Modifier,
    style: ShadowedBoxStyle = ShadowedBoxDefaults.defaultStyle(),
    reverse: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    with(style) {
        val initialSpace = remember { space }

        Box(
            modifier = modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Max)
                .padding(
                    top = initialSpace,
                    end = if (!reverse) initialSpace else 0.dp,
                    start = if (!reverse) 0.dp else initialSpace
                )
        ) {
            Canvas(
                modifier = Modifier
                    .size(space)
                    .offset(y = -space)
                    .align(
                        if (!reverse) Alignment.TopStart else Alignment.TopEnd
                    )
            ) {
                val path = Path().apply {
                    moveTo(
                        x = if (!reverse) size.width else 0f,
                        y = 0f
                    )
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    color = shadowColor,
                    style = Fill
                )
            }
            Canvas(
                modifier = Modifier
                    .size(space)
                    .offset(x = if (!reverse) space else -space)
                    .align(
                        if (!reverse) Alignment.BottomEnd else Alignment.BottomStart
                    )
            ) {
                val path = Path().apply {
                    moveTo(
                        x = if (!reverse) 0f else size.width,
                        y = size.height
                    )
                    lineTo(
                        x = if (!reverse) size.width else 0f,
                        y = 0f
                    )
                    lineTo(
                        x = if (!reverse) 0f else size.width,
                        y = 0f
                    )
                    close()
                }
                drawPath(
                    path = path,
                    color = shadowColor,
                    style = Fill
                )
            }
            Box(
                modifier = Modifier
                    .background(shadowColor)
                    .fillMaxSize()
                    .offset(
                        x = if (!reverse) space else -space,
                        y = -space
                    )
                    .sizeIn(
                        minWidth = 50.dp,
                        minHeight = 25.dp
                    )
                    .fillMaxSize()
                    .border(
                        width = borderWidth,
                        color = borderColor
                    )
                    .background(backgroundColor)
                    .padding(contentPadding)
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
private fun ShadowedBoxPreview() {
    KonnektTheme {
        Scaffold {
            ShadowedBox(
                modifier = Modifier.padding(it),
            ) {
                Text("A Text")
            }
        }
    }
}