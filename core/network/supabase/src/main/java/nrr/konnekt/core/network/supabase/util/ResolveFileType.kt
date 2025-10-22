package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.util.FileType
import nrr.konnekt.core.network.upload.domain.FileUploadConstraints

internal fun resolveFileType(
    fileExtension: String,
    fileUploadConstraints: FileUploadConstraints
) = isExtensionAllowed(fileExtension, fileUploadConstraints)
    ?.toString()
    ?.lowercase()
    ?: throw IllegalArgumentException("Invalid file extension: $fileExtension")

private fun isExtensionAllowed(
    extension: String,
    fileUploadConstraints: FileUploadConstraints
): AttachmentType? = with(fileUploadConstraints) {
    if (allowedImageTypes.mapExtensions().contains(extension)) AttachmentType.IMAGE
    else if (allowedVideoTypes.mapExtensions().contains(extension)) AttachmentType.VIDEO
    else if (allowedAudioTypes.mapExtensions().contains(extension)) AttachmentType.AUDIO
    else if (allowedDocumentTypes.mapExtensions().contains(extension)) AttachmentType.DOCUMENT
    else null
}

private fun List<FileType>.mapExtensions() = map { it.extension }