package nrr.konnekt.core.ui.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import nrr.konnekt.core.ui.util.AlertDialogDefaults
import nrr.konnekt.core.ui.util.AlertDialogStyle

@Composable
fun ActionAlertDialog(
    alert: Alert?,
    onDismissRequest: (Alert?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismiss = { onDismissRequest(null) }

    alert?.let { alert ->
        val style = alert.style ?: AlertDialogDefaults.defaultStyle()

        AlertDialog(
            onDismissRequest = dismiss,
            modifier = modifier,
            title = alert.title,
            message = alert.message,
            confirmButton = {
                TextButton(
                    onClick = {
                        alert.onConfirm()
                        dismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = style.confirmButtonContentColor
                    )
                ) {
                    Text(alert.confirmText ?: "Confirm")
                }
            },
            cancelButton = {
                TextButton(
                    onClick = dismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = style.cancelButtonContentColor
                    )
                ) {
                    Text(alert.cancelText ?: "Cancel")
                }
            },
            style = style
        )
    }
}

data class Alert(
    val onConfirm: () -> Unit,
    val title: String? = null,
    val message: String? = null,
    val confirmText: String? = null,
    val cancelText: String? = null,
    val style: AlertDialogStyle? = null
)