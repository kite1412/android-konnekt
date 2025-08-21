package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.AttachmentType

internal object FileType {
    val allowedImageExtensions = listOf(
        "jpg",
        "jpeg",
        "png",
        "heif",
        "heic"
    )
    val allowedVideoExtensions = listOf(
        "mp4"
    )
    val allowedAudioExtensions = listOf(
        "mp3"
    )
    val allowedDocumentExtensions = listOf(
        "pdf",
        "doc",
        "docx",
        "ppt",
        "pptx",
        "xls",
        "xlsx",
        "txt",
    )

    enum class Enum {
        IMAGE,
        VIDEO,
        AUDIO,
        PLAIN_TEXT,
        APPLICATION;

        override fun toString(): String =
            when (this) {
                IMAGE -> "image"
                VIDEO -> "video"
                AUDIO -> "audio"
                PLAIN_TEXT -> "text/plain"
                APPLICATION -> "application"
            }
    }
}

internal fun rawToAttachmentType(string: String) =
    when (string) {
        FileType.Enum.IMAGE.toString() -> AttachmentType.IMAGE
        FileType.Enum.VIDEO.toString() -> AttachmentType.VIDEO
        FileType.Enum.AUDIO.toString() -> AttachmentType.AUDIO
        FileType.Enum.PLAIN_TEXT.toString() -> AttachmentType.DOCUMENT
        FileType.Enum.APPLICATION.toString() -> AttachmentType.DOCUMENT
        else -> throw IllegalArgumentException("Invalid file type: $string")
    }