package nrr.konnekt.core.ui.util

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.ui.compositionlocal.LocalFileResolver

@Composable
fun rememberResolvedFile(
    path: String?,
    fileResolver: FileResolver = LocalFileResolver.current
): State<ImageBitmap?> = produceState(null) {
    value = path?.let {
        fileResolver.resolveFile(it)?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
    }
}