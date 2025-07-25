package nrr.konnekt.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.R
import nrr.konnekt.designsystem.theme.Gray
import nrr.konnekt.designsystem.theme.KonnektTheme

@Composable
fun ShadowedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here",
    label: String? = null,
    labelTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    actions: (@Composable () -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    singleLine: Boolean = true,
    shadowColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    space: Dp = 8.dp,
) {
    val valueIsEmpty = value.isEmpty()
    val adjustTextStyle = textStyle.copy(
        color = LocalContentColor.current
    )

    ShadowedBox(
        modifier = modifier
            .sizeIn(
                minWidth = 200.dp
            ),
        shadowColor = shadowColor,
        backgroundColor = backgroundColor,
        contentPadding = contentPadding,
        space = space
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                label?.let {
                    Text(
                        text = it,
                        style = labelTextStyle
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = adjustTextStyle,
                    decorationBox = {
                        Box {
                            if (valueIsEmpty) Text(
                                text = placeholder,
                                style = adjustTextStyle.copy(
                                    color = Gray
                                )
                            )
                            it()
                        }
                    },
                    cursorBrush = SolidColor(adjustTextStyle.color),
                    singleLine = singleLine
                )
            }
            actions?.invoke()
        }
    }
}

@Preview
@Composable
private fun ShadowedTextFieldPreview() {
    var text by remember { mutableStateOf("") }

    KonnektTheme {
        Scaffold {
            ShadowedTextField(
                value = text,
                onValueChange = { t -> text = t },
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth(),
                label = "A Label",
                textStyle = MaterialTheme.typography.bodyMedium,
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(2) {
                            Icon(
                                painter = painterResource(R.drawable.konnekt),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}