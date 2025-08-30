package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.AttachmentType

internal enum class FileType {
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

internal fun rawToAttachmentType(type: String) =
    when (type) {
        FileType.IMAGE.toString() -> AttachmentType.IMAGE
        FileType.VIDEO.toString() -> AttachmentType.VIDEO
        FileType.AUDIO.toString() -> AttachmentType.AUDIO
        FileType.PLAIN_TEXT.toString() -> AttachmentType.DOCUMENT
        FileType.APPLICATION.toString() -> AttachmentType.DOCUMENT
        else -> throw IllegalArgumentException("Invalid file type: $type")
    }