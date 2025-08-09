package nrr.konnekt.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.R
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldDefaults
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldStyle

@Composable
fun ShadowedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here",
    label: String? = null,
    actions: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    style: ShadowedTextFieldStyle = ShadowedTextFieldDefaults.defaultStyle()
) {
    val valueIsEmpty = value.isEmpty()
    val adjustTextStyle = style.textStyle.copy(
        color = LocalContentColor.current
    )

    ShadowedBox(
        modifier = modifier
            .sizeIn(
                minWidth = 200.dp
            )
            .fillMaxWidth(),
        shadowColor = style.shadowColor,
        backgroundColor = style.backgroundColor,
        contentPadding = style.contentPadding,
        space = style.space
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
                        style = style.labelTextStyle
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
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
                    singleLine = singleLine,
                    visualTransformation = visualTransformation
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