package nrr.konnekt.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldDefaults
import nrr.konnekt.core.designsystem.util.ShadowedTextFieldStyle
import nrr.konnekt.core.designsystem.util.TextFieldErrorIndicator

@Composable
fun SecureShadowedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Enter text here",
    errorIndicators: List<TextFieldErrorIndicator>? = null,
    label: String? = null,
    actions: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    style: ShadowedTextFieldStyle = ShadowedTextFieldDefaults.defaultStyle()
) {
    var hide by rememberSaveable { mutableStateOf(true) }

    ShadowedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        errorIndicators = errorIndicators,
        label = label,
        actions = {
            actions?.invoke()
            AnimatedContent(
               targetState = hide
            ) {
                Icon(
                    painter = painterResource(
                        id = if (it) KonnektIcon.eye else KonnektIcon.eyeClosed
                    ),
                    contentDescription = if (hide) "Show Password" else "Hide Password",
                    modifier = Modifier.clickable {
                        hide = !hide
                    }
                )
            }
        },
        singleLine = singleLine,
        visualTransformation = if (hide) HideContent else VisualTransformation.None,
        style = style
    )
}

private object HideContent : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString("*".repeat(text.length)),
            offsetMapping = OffsetMapping.Identity
        )
    }
}