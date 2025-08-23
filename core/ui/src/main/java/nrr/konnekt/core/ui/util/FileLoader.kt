package nrr.konnekt.core.ui.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.ui.compositionlocal.LocalFileResolver

@Composable
fun rememberResolvedFile(
    path: String?,
    fileResolver: FileResolver = LocalFileResolver.current
): State<ByteArray?> = remember(path) {
    mutableStateOf<ByteArray?>(null)
}.also {
    LaunchedEffect(path) {
        if (path != null && it.value == null) {
            Log.d("rememberResolvedFile", "resolving file: $path")
            it.value = fileResolver.resolveFile(path)
        }
    }
}