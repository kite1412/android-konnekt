package nrr.konnekt.feature.conversation.util

import androidx.compose.ui.graphics.ImageBitmap
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.model.AttachmentType

internal data class ComposerAttachment(
    val fileName: String,
    val type: AttachmentType,
    val content: ByteArray,
    val size: Long? = null,
    val thumbnail: ImageBitmap? = null,
    val durationSeconds: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComposerAttachment

        if (fileName != other.fileName) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + (content.contentHashCode())
        return result
    }
}

internal fun ComposerAttachment.toFileUpload(): FileUpload =
    FileUpload(
        fileName = fileName,
        fileExtension = fileName.substringAfterLast('.'),
        content = content
    )