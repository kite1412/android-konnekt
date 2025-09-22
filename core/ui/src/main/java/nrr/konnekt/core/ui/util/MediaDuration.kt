package nrr.konnekt.core.ui.util

import android.content.Context
import android.media.MediaMetadataRetriever
import java.io.File
import java.io.FileOutputStream


fun Context.getAudioDurationMs(
    bytes: ByteArray,
    extension: String = ".mp3"
): Long {
    val retriever = MediaMetadataRetriever()
    var tempFile: File? = null

    try {
        tempFile = File.createTempFile("temp_audio", extension, cacheDir)
        FileOutputStream(tempFile).use {
            it.write(bytes)
        }
        retriever.setDataSource(tempFile.absolutePath)
        return retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLong() ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        return 0L
    } finally {
        retriever.release()
        tempFile?.delete()
    }
}