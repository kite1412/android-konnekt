package nrr.konnekt.core.network.upload.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import nrr.konnekt.core.network.upload.domain.FileUploadConstraints
import nrr.konnekt.core.network.upload.util.ValidationResult.Invalid
import nrr.konnekt.core.network.upload.util.ValidationResult.Valid
import nrr.konnekt.core.network.upload.util.exception.FileUploadConstraintViolationException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUploadValidator @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val constraints: FileUploadConstraints
) {
    operator fun invoke(uri: Uri): ValidationResult = try {
        with(context) {
            checkSize(getFileSize(uri))
            checkMimeType(
                getMimeType(uri) ?: throw FileUploadConstraintViolationException(
                    message = "Can't resolve mime type of uri $uri",
                    reason = ViolationReason.INVALID_MIME_TYPE
                )
            )

            Valid
        }
    } catch (e: FileUploadConstraintViolationException) {
        e.printStackTrace()
        Invalid(e)
    } catch (e: RuntimeException) {
        e.printStackTrace()
        Invalid(
            FileUploadConstraintViolationException(
                message = "File not found",
                reason = ViolationReason.FILE_NOT_FOUND
            )
        )
    }

    private fun Context.getFileSize(uri: Uri): Int {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                it.moveToFirst()
                it.getLong(sizeIndex).toInt()
            } else {
                -1
            }
        } ?: -1
    }

    private fun Context.getMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.getType(uri)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase(Locale.getDefault()))
        }
    }

    private fun checkSize(bytesLength: Int) {
        if (bytesLength < 0)
            throw FileUploadConstraintViolationException(
                message = "File size is invalid",
                reason = ViolationReason.FILE_SIZE_INVALID
            )
        if (bytesLength > constraints.maxSizeBytes)
            throw FileUploadConstraintViolationException(
                message = "File size is too large, max size: ${getMB(constraints.maxSizeBytes)} MB",
                reason = ViolationReason.FILE_SIZE_TOO_LARGE
            )
    }

    private fun checkMimeType(mimeType: String): Unit = with(constraints) {
        (if (mimeType.startsWith("image/"))
            allowedImageTypes.firstOrNull {
                it.mimeType == mimeType
            }
        else if (mimeType.startsWith("video/"))
            allowedVideoTypes.firstOrNull {
                it.mimeType == mimeType
            }
        else if (mimeType.startsWith("audio/"))
            allowedAudioTypes.firstOrNull {
                it.mimeType == mimeType
            }
        else allowedDocumentTypes.firstOrNull {
            it.mimeType == mimeType
        }) ?: throw FileUploadConstraintViolationException(
            message = "Unsupported mime type: $mimeType",
            reason = ViolationReason.UNSUPPORTED_MIME_TYPE
        )
    }
}