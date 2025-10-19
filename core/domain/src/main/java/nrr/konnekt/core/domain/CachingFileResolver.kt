package nrr.konnekt.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nrr.konnekt.core.domain.util.DownloadStatus

class CachingFileResolver(
    private val delegate: FileResolver,
    private val cache: FileCache
) : FileResolver {

    override suspend fun resolveFile(path: String): ByteArray? {
        if (path in cache) return cache[path]
        val bytes = delegate.resolveFile(path)
        return bytes?.let {
            cache[path] = it
            it
        }
    }

    override fun resolveFileAsFlow(path: String): Flow<DownloadStatus> = flow {
        if (path in cache) cache[path]?.let {
            emit(DownloadStatus.Complete(it))
            return@flow
        }

        delegate.resolveFileAsFlow(path).collect {
            emit(it)
            if (it is DownloadStatus.Complete) cache[path] = it.bytes
        }
    }
}