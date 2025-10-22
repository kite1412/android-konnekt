package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import nrr.konnekt.core.network.upload.domain.FileResolver
import nrr.konnekt.core.network.upload.domain.util.DownloadStatus

val LocalFileResolver = compositionLocalOf<FileResolver> {
    object : FileResolver {
        override suspend fun resolveFile(path: String): ByteArray? = null

        override fun resolveFileAsFlow(path: String): Flow<DownloadStatus> =
            flowOf(DownloadStatus.Error("no file resolver"))
    }
}