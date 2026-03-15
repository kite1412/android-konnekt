package nrr.konnekt.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.util.KonnektIcon

@Composable
fun SimpleHeader(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textStyle = MaterialTheme.typography.titleMedium

            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    painter = painterResource(KonnektIcon.chevronLeft),
                    contentDescription = "back",
                    modifier = Modifier.size(textStyle.fontSize.value.dp)
                )
            }
            Text(
                text = title,
                style = textStyle.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}