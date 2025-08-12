package nrr.konnekt.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableShadowedButtons(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    buttonStyle: ShadowedButtonStyle = ButtonDefaults.defaultShadowedStyle()
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.takeIf { it.isNotEmpty() }?.forEach {
            SelectableShadowedButton(
                text = it,
                selected = it == selectedOption,
                onClick = { o -> onOptionSelected(o) },
                style = buttonStyle
            )
        }
    }
}

@Composable
private fun SelectableShadowedButton(
    text: String,
    selected: Boolean,
    onClick: (String) -> Unit,
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
                onClick(text)
            },
        style = style.copy(
            space = style.space * spaceFactor.value
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
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