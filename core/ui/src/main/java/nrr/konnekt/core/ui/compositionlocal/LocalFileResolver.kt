package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import nrr.konnekt.core.domain.FileResolver

val LocalFileResolver = compositionLocalOf {
    FileResolver { null }
}