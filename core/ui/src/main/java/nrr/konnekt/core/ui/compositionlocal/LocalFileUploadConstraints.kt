package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import nrr.konnekt.core.domain.FileUploadConstraints
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.util.FileType

val LocalFileUploadConstraints = compositionLocalOf<FileUploadConstraints> {
    object : FileUploadConstraints {
        override val maxSizeBytes: Long
            get() = 0L
        override val allowedImageTypes: List<FileType>
            get() = emptyList()
        override val allowedVideoTypes: List<FileType>
            get() = emptyList()
        override val allowedAudioTypes: List<FileType>
            get() = emptyList()
        override val allowedDocumentTypes: List<FileType>
            get() = emptyList()

        override fun isMimeTypeAllowed(mimeType: String): AttachmentType? = null

        override fun isExtensionAllowed(extension: String): AttachmentType? = null
    }
}