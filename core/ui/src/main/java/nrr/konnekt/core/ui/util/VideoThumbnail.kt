package nrr.konnekt.core.ui.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream

fun Context.getVideoThumbnail(
    uri: Uri,
    retriever: MediaMetadataRetriever = MediaMetadataRetriever()
): ImageBitmap? = try {
    retriever.setDataSource(this, uri)
    retriever.getFrameAtTime(0)?.asImageBitmap()
} catch (e: Exception) {
    e.printStackTrace()
    null
} finally {
    retriever.release()
}

fun Context.getVideoThumbnail(
    bytes: ByteArray,
    retriever: MediaMetadataRetriever = MediaMetadataRetriever()
): ImageBitmap? {
    var tempFile: File? = null
    return try {
        tempFile = File.createTempFile("temp_video", ".mp4", cacheDir)
        FileOutputStream(tempFile).use {
            it.write(bytes)
        }
        retriever.setDataSource(tempFile.absolutePath)
        retriever.getFrameAtTime(0)?.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
        tempFile?.delete()
    }
}