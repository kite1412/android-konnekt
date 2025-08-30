package nrr.konnekt.feature.conversation.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.asImageBitmap
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.util.AllowedAttachmentExtension
import nrr.konnekt.core.ui.util.asImageBitmap
import nrr.konnekt.feature.conversation.exception.AttachmentContentException
import nrr.konnekt.feature.conversation.exception.AttachmentNameException
import nrr.konnekt.feature.conversation.exception.AttachmentTypeException
import nrr.konnekt.feature.conversation.exception.UriConversionException

internal fun Context.uriToComposerAttachment(uri: Uri): ComposerAttachment {
    var fileName: String? = null

    return try {
        fileName = getFileName(uri)
        val type = getAttachmentType(uri)
        val content = uriToByteArray(uri)

        ComposerAttachment(
            fileName = fileName,
            type = type,
            content = content,
            thumbnail = when (type) {
                AttachmentType.IMAGE -> content.asImageBitmap()
                AttachmentType.VIDEO -> {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(
                        this,
                        uri
                    )
                    val thumbnail = retriever.getFrameAtTime(0) // Get frame at 0 microsecond
                    retriever.release()
                    thumbnail?.asImageBitmap()
                }
                else -> null
            }
        )
    } catch (e: AttachmentNameException) {
        e.printStackTrace()
        throw UriConversionException(e.message)
    } catch (e: AttachmentTypeException) {
        e.printStackTrace()
        throw UriConversionException("${fileName}: " + e.message)
    } catch (e: AttachmentContentException) {
        e.printStackTrace()
        throw UriConversionException("${fileName}: " + e.message)
    }
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

private fun Context.getAttachmentType(uri: Uri): AttachmentType =
    with(contentResolver.getType(uri)) {
        val extension = this?.substringAfterLast('/')
            ?: throw AttachmentTypeException()
        val checkExtension = { type: AttachmentType ->
            checkExtension(extension, type)
        }

        when {
            startsWith("image/") ->
                checkExtension(AttachmentType.IMAGE)
            startsWith("video/") ->
                checkExtension(AttachmentType.VIDEO)
            startsWith("audio/") ->
                checkExtension(AttachmentType.AUDIO)
            startsWith("application/") ->
                checkExtension(AttachmentType.DOCUMENT)
            this == "text/plain" -> AttachmentType.DOCUMENT
            else -> null
        }
    } ?: throw AttachmentTypeException()

private fun checkExtension(extension: String, type: AttachmentType): AttachmentType =
    if (type == AttachmentType.IMAGE && AllowedAttachmentExtension.imageExtensions.contains(extension)) type
    else if (type == AttachmentType.VIDEO && AllowedAttachmentExtension.videoExtensions.contains(extension)) type
    else if (type == AttachmentType.AUDIO && AllowedAttachmentExtension.audioExtensions.contains(extension)) type
    else if (type == AttachmentType.DOCUMENT && AllowedAttachmentExtension.documentExtensions.contains(extension)) type
    else throw AttachmentTypeException("Unsupported file type")