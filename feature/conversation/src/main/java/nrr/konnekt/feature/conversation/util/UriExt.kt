package nrr.konnekt.feature.conversation.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.network.upload.util.FileUploadValidator
import nrr.konnekt.core.network.upload.util.ValidationResult
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.getFileName
import nrr.konnekt.core.ui.util.getVideoThumbnail
import nrr.konnekt.core.ui.util.uriToByteArray
import nrr.konnekt.feature.conversation.exception.AttachmentTypeException

internal fun Context.uriToComposerAttachment(
    uri: Uri,
    fileUploadValidator: FileUploadValidator
): ComposerAttachment = try {
    val validationResult = fileUploadValidator(uri)
    if (validationResult is ValidationResult.Invalid)
        throw validationResult.exception

    val content = uriToByteArray(uri)
    val fileName = getFileName(uri)
    val type = getAttachmentType(uri)
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

private fun Context.getAttachmentType(uri: Uri): AttachmentType =
    contentResolver.getType(uri)?.let {
        if (it.startsWith("image/"))
            AttachmentType.IMAGE
        else if (it.startsWith("video/"))
            AttachmentType.VIDEO
        else if (it.startsWith("audio/"))
            AttachmentType.AUDIO
        else AttachmentType.DOCUMENT
    } ?: throw AttachmentTypeException()