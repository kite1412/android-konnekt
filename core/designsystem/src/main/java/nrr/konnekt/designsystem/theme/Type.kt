package nrr.konnekt.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import nrr.konnekt.designsystem.R

private val Nunito = FontFamily(
    Font(
        resId = R.font.nunito_regular,
        style = FontStyle.Normal,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.nunito_bold,
        style = FontStyle.Normal,
        weight = FontWeight.Bold
    ),
    Font(
        resId = R.font.nunito_italic,
        style = FontStyle.Italic,
        weight = FontWeight.Normal
    ),
)

const val NORMAL_LINE_HEIGHT_MULTIPLIER = 1.4f

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = with(18) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    },
    bodyMedium = with(16) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    },
    bodySmall = with(14) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    },
)