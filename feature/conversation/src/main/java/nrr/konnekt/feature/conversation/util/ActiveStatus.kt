package nrr.konnekt.feature.conversation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.theme.Gray
import nrr.konnekt.core.designsystem.theme.GreenPrimaryDarken
import nrr.konnekt.core.model.util.now
import kotlin.time.Instant

internal object ActiveStatus {
    @Composable
    fun Personal(
        isActive: Boolean,
        lastActive: Instant,
        modifier: Modifier = Modifier
    ) {
        with(LocalTextStyle.current) {
            if (isActive) Active(
                text = "Active now",
                isActive = true,
                modifier = modifier,
                textStyle = this
            ) else Text(
                text = "Last active ${resolvePersonalLastActive(lastActive)} ago",
                modifier = modifier,
                style = this@with.copy(
                    color = Gray,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }

    @Composable
    fun Group(
        totalActiveParticipants: Int,
        modifier: Modifier = Modifier
    ) {
        Active(
            text = if (totalActiveParticipants > 0) "$totalActiveParticipants Active now"
                else "Nobody active",
            isActive = totalActiveParticipants > 0,
            modifier = modifier,
            textStyle = LocalTextStyle.current.copy(
                fontStyle = if (totalActiveParticipants > 0) FontStyle.Normal
                    else FontStyle.Italic,
                color = if (totalActiveParticipants > 0) LocalContentColor.current
                    else Gray
            )
        )
    }

    private fun resolvePersonalLastActive(lastActive: Instant) =
        with(now() - lastActive) {
            if (inWholeDays > 28) "a long time"
            else if (inWholeDays > 7) "${(inWholeDays / 7).toInt()} weeks"
            else if (inWholeDays > 0) "$inWholeDays days"
            else if (inWholeHours > 0) "$inWholeHours hours"
            else if (inWholeMinutes > 0) "$inWholeMinutes minutes"
            else "a moment"
        }

    @Composable
    private fun Active(
        text: String,
        isActive: Boolean,
        modifier: Modifier = Modifier,
        textStyle: TextStyle = MaterialTheme.typography.bodySmall
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(textStyle.fontSize.value.dp)
                    .clip(CircleShape)
                    .background(if (isActive) GreenPrimaryDarken else DarkGray)
            )
            Text(
                text = text,
                style = textStyle
            )
        }
    }
}