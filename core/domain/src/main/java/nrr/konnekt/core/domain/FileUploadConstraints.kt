package nrr.konnekt.core.domain

import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.util.FileType

/**
 * Constraints for uploading files.
 */
interface FileUploadConstraints {
    /**
     * The maximum size of the file in bytes.
     */
    val maxSizeBytes: Long

    /**
     * The allowed image types.
     */
    val allowedImageTypes: List<FileType>

    /**
     * The allowed video types.
     */
    val allowedVideoTypes: List<FileType>

    /**
     * The allowed audio types.
     */
    val allowedAudioTypes: List<FileType>

    /**
     * The allowed document types.
     */
    val allowedDocumentTypes: List<FileType>

    /**
     * The allowed file extensions.
     *
     * @param mimeType The MIME type of the file.
     * @return The [AttachmentType] for the given MIME type or null if MIME type is not allowed.
     */
    fun isMimeTypeAllowed(mimeType: String): AttachmentType?

    /**
     * The allowed file extensions.
     *
     * @param extension The extension of the file.
     * @return The [AttachmentType] for the given extension or null if extension is not allowed.
     */
    fun isExtensionAllowed(extension: String): AttachmentType?
}
