package nrr.konnekt.designsystem

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import nrr.konnekt.designsystem.theme.DarkGray
import nrr.konnekt.designsystem.theme.Gray

object KonnektDefaults {
    @Composable
    fun buttonColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = LocalContentColor.current,
        disabledContainerColor = DarkGray,
        disabledContentColor = Gray
    )
}