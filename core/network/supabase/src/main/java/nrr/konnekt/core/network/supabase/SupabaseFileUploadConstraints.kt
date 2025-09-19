package nrr.konnekt.core.network.supabase

import nrr.konnekt.core.domain.FileUploadConstraints
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.util.DefaultAllowedFileType
import nrr.konnekt.core.model.util.FileType
import javax.inject.Inject

class SupabaseFileUploadConstraints @Inject constructor() : FileUploadConstraints {
    // 50 MB
    override val maxSizeBytes: Long = 1_048_576L * 50
    override val allowedImageTypes: List<FileType> = DefaultAllowedFileType.imageTypes
    override val allowedVideoTypes: List<FileType> = DefaultAllowedFileType.videoTypes
    override val allowedAudioTypes: List<FileType> = DefaultAllowedFileType.audioTypes
    override val allowedDocumentTypes: List<FileType> = DefaultAllowedFileType.documentTypes

    override fun isMimeTypeAllowed(mimeType: String): AttachmentType? =
        DefaultAllowedFileType.isMimeTypeAllowed(mimeType)

    override fun isExtensionAllowed(extension: String): AttachmentType? =
        DefaultAllowedFileType.isExtensionAllowed(extension)
}