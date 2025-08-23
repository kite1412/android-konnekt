package nrr.konnekt.core.ui.util

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap

fun ByteArray.asImageBitmap() =
    BitmapFactory
        .decodeByteArray(this, 0, this.size)
        .asImageBitmap()