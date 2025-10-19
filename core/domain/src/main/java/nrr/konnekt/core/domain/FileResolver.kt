package nrr.konnekt.core.domain

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.util.DownloadStatus

/**
 * Contract for resolving files from a given storage path into raw bytes.
 */
interface FileResolver {
    /**
     * Resolves a file from a given storage path into raw bytes.
     *
     * @param path The storage path of the file.
     * @return The raw bytes of the file, or null if the file could not be resolved.
     */
    suspend fun resolveFile(path: String): ByteArray?

    /**
     * Resolves a file from a given storage path and emits its download progress as a flow.
     *
     * @param path The storage path of the file.
     * @return A [Flow] that emits [DownloadStatus] updates representing the current state of the file resolution.
     */
    fun resolveFileAsFlow(path: String): Flow<DownloadStatus>
}