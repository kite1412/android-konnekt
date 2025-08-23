package nrr.konnekt.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.ui.compositionlocal.LocalFileResolver

@Composable
fun rememberResolvedFile(
    path: String?,
    fileResolver: FileResolver = LocalFileResolver.current
): State<ByteArray?> = produceState(null) {
    value = path?.let {
        fileResolver.resolveFile(it)
    }
}