package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import nrr.konnekt.core.network.upload.util.FileUploadValidator

val LocalFileUploadValidator = compositionLocalOf<FileUploadValidator> {
    throw RuntimeException("FileUploadValidator not provided")
}