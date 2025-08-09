package nrr.konnekt.core.ui.util

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import nrr.konnekt.core.domain.ImagePathResolver
import nrr.konnekt.core.ui.compositionlocal.LocalImagePathResolver

@Composable
fun rememberResolvedImage(
    path: String?,
    imageResolver: ImagePathResolver = LocalImagePathResolver.current
): State<ImageBitmap?> = produceState(null) {
    value = path?.let {
        imageResolver.resolveImage(it)?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
    }
}