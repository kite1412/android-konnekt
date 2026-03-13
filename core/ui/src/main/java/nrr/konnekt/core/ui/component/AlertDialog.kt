package nrr.konnekt.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.ui.util.AlertDialogDefaults
import nrr.konnekt.core.ui.util.AlertDialogStyle

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    style: AlertDialogStyle = AlertDialogDefaults.defaultStyle(),
    cancelButton: (@Composable () -> Unit)? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val clipShape = style.shape

            Column(
                modifier = modifier
                    .align(Alignment.Center)
                    .sizeIn(
                        maxWidth = 400.dp
                    )
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(clipShape)
                    .background(style.backgroundColor)
                    .border(
                        width = 2.dp,
                        color = style.borderColor,
                        shape = clipShape
                    )
                    .clickable(false) {}
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (title != null) Arrangement.SpaceBetween
                        else Arrangement.End
                ) {
                    title?.let { title ->
                        Text(
                            text = title,
                            modifier = Modifier.weight(0.9f),
                            style = style.titleStyle,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(KonnektIcon.x),
                            contentDescription = "cancel"
                        )
                    }
                }
                message?.let { message ->
                    Text(
                        text = message,
                        style = style.messageStyle
                    )
                }

                content?.invoke()

                if (confirmButton != null || cancelButton != null) Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    cancelButton?.let {
                        CompositionLocalProvider(LocalContentColor provides style.cancelButtonContentColor) {
                            it()
                        }
                    }
                    confirmButton?.let {
                        CompositionLocalProvider(LocalContentColor provides style.confirmButtonContentColor) {
                            it()
                        }
                    }
                }
            }
        }
    }
}