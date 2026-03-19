package nrr.konnekt.core.notification.util

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import androidx.core.graphics.createBitmap

internal fun Bitmap.toCircularBitmap(): Bitmap {
    val size = minOf(width, height)

    val output = createBitmap(size, size)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        shader = BitmapShader(this@toCircularBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius, paint)

    return output
}