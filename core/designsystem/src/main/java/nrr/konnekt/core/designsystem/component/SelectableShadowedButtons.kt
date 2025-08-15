package nrr.konnekt.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.ButtonDefaults
import nrr.konnekt.core.designsystem.util.ShadowedButtonStyle
import nrr.konnekt.core.designsystem.util.toShadowedBoxStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> SelectableShadowedButtons(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    buttonStyle: ShadowedButtonStyle = ButtonDefaults.defaultShadowedStyle()
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = contentPadding
    ) {
        options.takeIf { it.isNotEmpty() }?.let {
            items(it.size) { i ->
                SelectableShadowedButton(
                    option = it[i],
                    selected = it[i] == selectedOption,
                    onClick = { o -> onOptionSelected(o) },
                    style = buttonStyle
                )
            }
        }
    }
}

@Composable
private fun <T> SelectableShadowedButton(
    option: T,
    selected: Boolean,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    style: ShadowedButtonStyle = ButtonDefaults.defaultShadowedStyle()
) {
    val spaceFactor = remember { Animatable(1f) }

    LaunchedEffect(selected) {
        spaceFactor.animateTo(if (selected) 0.5f else 1f)
    }
    ShadowedBox(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = null
            ) {
                onClick(option)
            },
        style = style.toShadowedBoxStyle(
            enabled = true,
            space = style.space * spaceFactor.value
        )
    ) {
        Text(
            text = option.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun SelectableShadowedButtonsPreview() {
    var selected by rememberSaveable { mutableStateOf("Option 1") }

    KonnektTheme {
        SelectableShadowedButtons(
            options = listOf("Option 1", "Option 2", "Option 3"),
            selectedOption = selected,
            onOptionSelected = { selected = it }
        )
    }
}