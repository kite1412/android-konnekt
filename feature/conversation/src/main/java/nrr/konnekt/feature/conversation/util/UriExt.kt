package nrr.konnekt.feature.conversation.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import nrr.konnekt.core.domain.FileUploadConstraints
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.getFileName
import nrr.konnekt.core.ui.util.getVideoThumbnail
import nrr.konnekt.core.ui.util.uriToByteArray
import nrr.konnekt.feature.conversation.exception.AttachmentTypeException

internal fun Context.uriToComposerAttachment(
    uri: Uri,
    fileUploadConstraints: FileUploadConstraints
): ComposerAttachment = try {
    val content = uriToByteArray(uri)
    fileUploadConstraints.checkSize(content.size)

    val fileName = getFileName(uri)
    val type = getAttachmentType(uri, fileUploadConstraints)
    var thumbnail: ImageBitmap? = null
    var durationSeconds: Long? = null
    when (type) {
        AttachmentType.VIDEO -> {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, uri)
            thumbnail = getVideoThumbnail(uri)
            durationSeconds = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong()?.div(1000L)
            retriever.release()
        }
        else -> {}
    }

    ComposerAttachment(
        fileName = fileName,
        type = type,
        content = content,
        thumbnail = when (type) {
            AttachmentType.IMAGE -> content.asImageBitmap()
            AttachmentType.VIDEO -> thumbnail
            else -> null
        },
        durationSeconds = durationSeconds
    )
} catch (e: RuntimeException) {
    e.printStackTrace()
    throw e
}

private fun Context.getAttachmentType(
    uri: Uri,
    fileUploadConstraints: FileUploadConstraints
): AttachmentType =
    contentResolver.getType(uri)?.let(fileUploadConstraints::isMimeTypeAllowed)
        ?: throw AttachmentTypeException()