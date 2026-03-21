package nrr.konnekt.core.notification.util

import android.graphics.BitmapFactory
import nrr.konnekt.core.network.upload.util.CachingFileResolver

internal suspend fun CachingFileResolver.getCircularBitmap(imagePath: String?) = imagePath?.let { imagePath ->
    val bytes = resolveFile(imagePath)

    bytes?.let { bytes ->
        BitmapFactory
            .decodeByteArray(
                /*data = */bytes,
                /*offset = */0,
                /*length = */bytes.size
            )
            .toCircularBitmap()
    }
}