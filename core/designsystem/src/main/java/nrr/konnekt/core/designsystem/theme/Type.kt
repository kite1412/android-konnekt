package nrr.konnekt.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import nrr.konnekt.core.designsystem.R

val RubikIso = FontFamily(
    Font(resId = R.font.rubik_iso_regular)
)

private val Nunito = FontFamily(
    Font(
        resId = R.font.nunito_regular,
        style = FontStyle.Normal,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.nunito_semibold,
        style = FontStyle.Normal,
        weight = FontWeight.SemiBold
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
    )
)

const val NORMAL_LINE_HEIGHT_MULTIPLIER = 1.4f

val Typography = Typography(
    bodyLarge = with(20) {
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
    bodySmall = with(12) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    },
    titleSmall = with(24) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    },
    titleMedium = with(32) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    },
    titleLarge = with(40) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = this.sp,
            lineHeight = (this * NORMAL_LINE_HEIGHT_MULTIPLIER).sp,
            letterSpacing = 0.5.sp
        )
    }
)