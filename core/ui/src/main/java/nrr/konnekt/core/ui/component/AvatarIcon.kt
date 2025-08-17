package nrr.konnekt.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nrr.konnekt.core.ui.util.getLetterColor
import nrr.konnekt.core.ui.util.rememberResolvedImage

@Composable
fun AvatarIcon(
    name: String,
    modifier: Modifier = Modifier,
    iconPath: String? = null,
    diameter: Dp = 40.dp
) {
    val icon by rememberResolvedImage(iconPath)

    Box(
        modifier = modifier
            .size(diameter)
            .clip(CircleShape)
    ) {
        icon?.let {
            Image(
                bitmap = it,
                contentDescription = "chat icon",
                modifier = Modifier.fillMaxSize()
            )
        } ?: with(name.firstOrNull()) char@{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(getLetterColor()),
                contentAlignment = Alignment.Center
            ) {
                this@char?.let {
                    Text(
                        text = it.toString(),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = (diameter.value / 2f).sp
                        )
                    )
                }
            }
        }
    }
}