package nrr.konnekt.core.ui.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import nrr.konnekt.core.ui.UriException
import nrr.konnekt.core.ui.UriExceptionReason

fun Context.getFileName(uri: Uri): String {
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

    return name ?: throw UriException(
        message = "Fail to resolve file name from Uri: $uri",
        reason = UriExceptionReason.INVALID_FILE_NAME
    )
}

fun Context.uriToByteArray(uri: Uri): ByteArray =
    try {
        contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw UriException(
            message = "Fail to read bytes from Uri: $uri",
            reason = UriExceptionReason.INVALID_CONTENT
        )
    } catch (e: Exception) {
        e.printStackTrace()
        throw UriException(
            message = "Fail to read bytes from Uri: $uri",
            reason = UriExceptionReason.INVALID_CONTENT
        )
    }