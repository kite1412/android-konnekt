package nrr.konnekt.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.designsystem.theme.KonnektTheme

/**
 * @param space - space between shadow and content
 */
@Composable
fun ShadowedBox(
    modifier: Modifier = Modifier,
    shadowColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    space: Dp = 2.dp,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    content: @Composable () -> Unit
) {
    val initialSpace = remember { space }

    Box(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
            .padding(
                top = initialSpace.value.dp,
                end = initialSpace.value.dp
            )
    ) {
        Canvas(
            modifier = Modifier
                .size(space)
                .offset(y = -space)
                .align(Alignment.TopStart)
        ) {
            val path = Path().apply {
                moveTo(size.width, 0f)
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
                .offset(x = space)
                .align(Alignment.BottomEnd)
        ) {
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width, 0f)
                lineTo(0f, 0f)
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
        ) {
            Box(
                modifier = Modifier
                    .offset(x = space, y = -space)
                    .sizeIn(
                        minWidth = 50.dp,
                        minHeight = 25.dp
                    )
                    .border(
                        width = 2.dp,
                        color = shadowColor
                    )
                    .background(backgroundColor)
                    .padding(contentPadding)
            ) {
                content()
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
                space = 4.dp
            ) {
                Text("A Text")
            }
        }
    }
}