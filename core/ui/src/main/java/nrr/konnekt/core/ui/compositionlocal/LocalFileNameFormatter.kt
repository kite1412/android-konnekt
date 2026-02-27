package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import nrr.konnekt.core.network.upload.domain.FileNameFormatter

val LocalFileNameFormatter = compositionLocalOf<FileNameFormatter> {
    object : FileNameFormatter {
        override fun format(rawName: String, ext: String): String = rawName
        override fun restore(formattedName: String): String = formattedName
    }
}