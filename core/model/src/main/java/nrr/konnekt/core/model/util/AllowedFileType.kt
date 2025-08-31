package nrr.konnekt.core.model.util

import nrr.konnekt.core.model.AttachmentType

object AllowedFileType {
    val imageTypes = listOf(
        FileType("image/jpeg", "jpg"),
        FileType("image/jpeg", "jpeg"),
        FileType("image/png", "png"),
        FileType("image/heif", "heif"),
        FileType("image/heic", "heic"),
    )
    val videoTypes = listOf(
        FileType("video/mp4", "mp4")
    )
    val audioTypes = listOf(
        FileType("audio/mpeg", "mp3")
    )
    val documentTypes = listOf(
        FileType("application/pdf", "pdf"),
        FileType("application/msword", "doc"),
        FileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
        FileType("application/vnd.ms-excel", "xls"),
        FileType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
        FileType("application/vnd.ms-powerpoint", "ppt"),
        FileType("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
        FileType("application/zip", "zip"),
        FileType("plain/text", "txt")
    )

    fun isMimeTypeAllowed(mimeType: String): AttachmentType? =
        if (mimeType.startsWith("image/"))
            imageTypes.firstOrNull {
                it.mimeType == mimeType
            }?.let { AttachmentType.IMAGE }
        else if (mimeType.startsWith("video/"))
            videoTypes.firstOrNull {
                it.mimeType == mimeType
            }?.let { AttachmentType.VIDEO }
        else if (mimeType.startsWith("audio/"))
            audioTypes.firstOrNull {
                it.mimeType == mimeType
            }?.let { AttachmentType.AUDIO }
        else documentTypes.firstOrNull {
            it.mimeType == mimeType
        }?.let { AttachmentType.DOCUMENT }

    fun isExtensionAllowed(extension: String): AttachmentType? =
        if (imageTypes.mapExtensions().contains(extension)) AttachmentType.IMAGE
        else if (videoTypes.mapExtensions().contains(extension)) AttachmentType.VIDEO
        else if (audioTypes.mapExtensions().contains(extension)) AttachmentType.AUDIO
        else if (documentTypes.mapExtensions().contains(extension)) AttachmentType.DOCUMENT
        else null

    private fun List<FileType>.mapExtensions() = map { it.extension }
}