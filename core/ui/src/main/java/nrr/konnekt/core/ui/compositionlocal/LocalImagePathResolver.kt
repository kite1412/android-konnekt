package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import nrr.konnekt.core.domain.ImagePathResolver

val LocalImagePathResolver = compositionLocalOf {
    ImagePathResolver { null }
}