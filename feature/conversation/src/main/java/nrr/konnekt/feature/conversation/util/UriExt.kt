package nrr.konnekt.feature.conversation.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.ImageBitmap
import nrr.konnekt.core.domain.FileUploadConstraints
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.core.ui.util.getVideoThumbnail
import nrr.konnekt.feature.conversation.exception.AttachmentContentException
import nrr.konnekt.feature.conversation.exception.AttachmentNameException
import nrr.konnekt.feature.conversation.exception.AttachmentSizeException
import nrr.konnekt.feature.conversation.exception.AttachmentTypeException
import nrr.konnekt.feature.conversation.exception.UriConversionException

internal fun Context.uriToComposerAttachment(
    uri: Uri,
    fileUploadConstraints: FileUploadConstraints
): ComposerAttachment {
    var fileName: String? = null

    return try {
        fileName = getFileName(uri)
        checkSize(uri, fileUploadConstraints)

        val type = getAttachmentType(uri, fileUploadConstraints)
        val content = uriToByteArray(uri)
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
    } catch (e: AttachmentNameException) {
        e.printStackTrace()
        throw UriConversionException(e.message)
    } catch (e: RuntimeException) {
        e.printStackTrace()
        throw UriConversionException("${fileName}: " + e.message)
    }
}

private fun Context.checkSize(
    uri: Uri,
    fileUploadConstraints: FileUploadConstraints
) = with(getFileSize(uri)) {
    if (this < 0)
        throw AttachmentSizeException("File size is invalid")
    if (this > fileUploadConstraints.maxSizeBytes)
        throw AttachmentSizeException(
            "File size is too large, max size: ${fileUploadConstraints.maxSizeBytes / 1_048_576} MB"
        )
}

private fun Context.getFileSize(uri: Uri): Long {
    val cursor = contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex != -1) {
            it.moveToFirst()
            it.getLong(sizeIndex)
        } else {
            -1L
        }
    } ?: -1L
}

private fun Context.getFileName(uri: Uri): String {
    var name: String? = null

    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1)
                    name = it.getString(nameIndex)
            }
        }
    }

    if (name == null) {
        name = uri.path?.let {
            val cut = it.lastIndexOf('/')
            if (cut != -1) it.substring(cut + 1) else it
        }
    }

    return name ?: throw AttachmentNameException()
}

private fun Context.uriToByteArray(uri: Uri): ByteArray =
    try {
        contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw AttachmentContentException()
    } catch (e: Exception) {
        e.printStackTrace()
        throw AttachmentContentException()
    }

private fun Context.getAttachmentType(
    uri: Uri,
    fileUploadConstraints: FileUploadConstraints
): AttachmentType =
    contentResolver.getType(uri)?.let(fileUploadConstraints::isMimeTypeAllowed)
        ?: throw AttachmentTypeException()