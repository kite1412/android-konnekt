package nrr.konnekt.core.domain

import nrr.konnekt.core.domain.exception.FileUploadConstraintViolationException
import nrr.konnekt.core.domain.exception.FileUploadConstraintViolationExceptionReason
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

    fun checkSize(bytesLength: Int) {
        if (bytesLength < 0)
            throw FileUploadConstraintViolationException(
                message = "File size is invalid",
                reason = FileUploadConstraintViolationExceptionReason.SIZE_INVALID
            )
        if (bytesLength > maxSizeBytes)
            throw FileUploadConstraintViolationException(
                message = "File size is too large, max size: ${maxSizeMB()} MB",
                reason = FileUploadConstraintViolationExceptionReason.SIZE_EXCEEDED
            )
    }

    fun maxSizeMB(): Long = maxSizeBytes / 1_048_576
}
