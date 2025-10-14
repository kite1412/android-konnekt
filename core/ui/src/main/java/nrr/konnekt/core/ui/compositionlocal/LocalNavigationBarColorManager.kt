package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import nrr.konnekt.core.ui.util.ValueManager

val LocalNavigationBarColorManager = compositionLocalOf<ValueManager<Color>> {
    object : ValueManager<Color> {
        override fun update(newValue: Color): Color = Color.Transparent

        override fun reset(): Color = Color.Transparent
    }
}