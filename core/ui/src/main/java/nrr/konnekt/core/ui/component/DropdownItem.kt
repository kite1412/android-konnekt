package nrr.konnekt.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current,
    iconId: Int? = null
) {
    val textStyle = LocalTextStyle.current

    CompositionLocalProvider(
        LocalContentColor provides contentColor,
    ) {
        Row(
            modifier = modifier
                .sizeIn(
                    maxWidth = 200.dp
                )
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onClick
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = textStyle.copy(
                    textAlign = if (iconId == null) TextAlign.End
                        else TextAlign.Start
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            iconId?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = text,
                    modifier = Modifier.size(textStyle.fontSize.value.dp * 1.5f)
                )
            }
        }
    }
}