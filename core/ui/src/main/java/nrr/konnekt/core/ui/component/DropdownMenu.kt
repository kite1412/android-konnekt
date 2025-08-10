package nrr.konnekt.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val contentPadding = 12.dp
    val outlineColor = MaterialTheme.colorScheme.outline

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .border(
                width = 2.dp,
                color = outlineColor,
                shape = shape
            )
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(contentPadding),
        shape = shape
    ) {
        CompositionLocalProvider(
            LocalContentColor provides outlineColor // match with outline color
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    contentPadding * 1.5f
                ),
                content = content
            )
        }
    }
}