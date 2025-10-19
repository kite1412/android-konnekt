package nrr.konnekt.core.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.net.URLConnection
import java.nio.file.NoSuchFileException

/**
 * Opens a cached file from the given [cachePath] using an external application/viewer
 * capable of handling its content type.
 */
fun Context.openCachedFileExternal(cachePath: String) {
    val file = File(cacheDir, cachePath)

    if (!file.exists()) throw NoSuchFileException("no such file with name: $cachePath")

    val pdfUri = FileProvider.getUriForFile(
        this,
        applicationContext.packageName + ".fileprovider",
        file
    )
    val contentType = URLConnection.guessContentTypeFromName(file.name)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(pdfUri, contentType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    startActivity(Intent.createChooser(intent, "Open file with"))
}