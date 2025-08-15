package nrr.konnekt.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.util.OutlinedTextFieldStyle
import nrr.konnekt.core.designsystem.util.TextFieldDefaults
import nrr.konnekt.core.designsystem.util.TextFieldErrorIndicator

@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here",
    errorIndicators: List<TextFieldErrorIndicator>? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    style: OutlinedTextFieldStyle = TextFieldDefaults.defaultOutlinedStyle(),
) {
    val errorColor = MaterialTheme.colorScheme.error
    val borderColor by animateColorAsState(
        targetValue = if (errorIndicators?.map { it.error }?.contains(true) == true)
            errorColor
        else style.outlineColor
    )
    var latestErrorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AnimatedVisibility(
            visible = errorIndicators != null && errorIndicators.any { it.error }
        ) {
            val message = errorIndicators?.firstOrNull { it.error }?.message
            message?.let {
                latestErrorMessage = it
            }
            Text(
                text = message ?: latestErrorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = errorColor
                )
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = borderColor
                )
                .background(style.backgroundColor)
                .padding(style.contentPadding),
            textStyle = style.textStyle,
            decorationBox = {
                Box(
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
}