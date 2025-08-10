package nrr.konnekt.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nrr.konnekt.core.designsystem.theme.KonnektTheme

/**
 * @param bounceBack - bounce back when clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShadowedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shadowColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    space: Dp = 4.dp,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    enabled: Boolean = true,
    bounceBack: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val spaceFactor = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    ShadowedBox(
        modifier = modifier
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = null
            ) {
                scope.launch {
                    spaceFactor.animateTo(0.5f)
                    onClick()
                    if (bounceBack) {
                        spaceFactor.animateTo(1f)
                    }
                }
            },
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        space = space * spaceFactor.value,
        contentPadding = contentPadding,
        content = content
    )
}

@Preview
@Composable
private fun ShadowedButtonPreview() {
    KonnektTheme {
        Scaffold {
            Row(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { i ->
                    ShadowedButton(
                        onClick = {},
                        space = 6.dp,
                        bounceBack = true
                    ) {
                        Text("Button")
                    }
                }
            }
        }
    }
}