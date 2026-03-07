package nrr.konnekt.core.ui.component

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nrr.konnekt.core.designsystem.theme.Red

@Composable
fun ActionAlertDialog(
    alert: Alert?,
    onDismissRequest: (Alert?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismiss = { onDismissRequest(null) }

    alert?.let { alert ->
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
                    }
                ) {
                    Text("Confirm")
                }
            },
            cancelButton = {
                TextButton(dismiss) {
                    Text(
                        text = "Cancel",
                        color = Red
                    )
                }
            }
        )
    }
}

data class Alert(
    val onConfirm: () -> Unit,
    val title: String? = null,
    val message: String? = null
)