package nrr.konnekt.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.util.OutlinedTextFieldStyle
import nrr.konnekt.core.designsystem.util.TextFieldDefaults

@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here",
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    style: OutlinedTextFieldStyle = TextFieldDefaults.defaultOutlinedStyle(),
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = style.outlineColor
            )
            .background(style.backgroundColor)
            .padding(style.contentPadding),
        textStyle = style.textStyle,
        decorationBox = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) Text(
                    text = placeholder,
                    style = style.textStyle.copy(
                        color = style.placeholderColor
                    )
                )
                it()
            }
        },
        singleLine = singleLine,
        maxLines = maxLines,
        cursorBrush = SolidColor(style.textStyle.color)
    )
}