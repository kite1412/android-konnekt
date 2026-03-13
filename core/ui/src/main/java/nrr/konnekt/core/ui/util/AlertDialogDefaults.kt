package nrr.konnekt.core.ui.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.DarkNavy
import nrr.konnekt.core.designsystem.theme.Red

object AlertDialogDefaults {
    @Composable
    fun defaultStyle(
        backgroundColor: Color = DarkNavy,
        borderColor: Color = MaterialTheme.colorScheme.background,
        shape: Shape = RoundedCornerShape(12.dp),
        titleStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Medium
        ),
        messageStyle: TextStyle = MaterialTheme.typography.bodyLarge,
        confirmButtonContentColor: Color = MaterialTheme.colorScheme.primary,
        cancelButtonContentColor: Color = Red
    ) = AlertDialogStyle(
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        shape = shape,
        titleStyle = titleStyle,
        messageStyle = messageStyle,
        confirmButtonContentColor = confirmButtonContentColor,
        cancelButtonContentColor = cancelButtonContentColor
    )
}

data class AlertDialogStyle(
    val backgroundColor: Color,
    val borderColor: Color,
    val shape: Shape,
    val titleStyle: TextStyle,
    val messageStyle: TextStyle,
    val confirmButtonContentColor: Color,
    val cancelButtonContentColor: Color
)