package nrr.konnekt.core.network.upload.domain

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
}
