package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.storage.DownloadStatus
import io.github.jan.supabase.storage.downloadAuthenticatedAsFlow
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import javax.inject.Inject
import nrr.konnekt.core.domain.util.DownloadStatus as _DownloadStatus

internal class SupabaseFileResolver @Inject constructor()
    : FileResolver {

    // expose for testing
    internal val iconBucketRegex = createBucketRegex(Bucket.ICON)
    internal val chatMediaBucketRegex = createBucketRegex(Bucket.CHAT_MEDIA)

    override suspend fun resolveFile(path: String): ByteArray? {
        if (!checkPath(path)) return null

        val info = getFileInfo(path)

        return supabaseClient
            .storage[info.bucket]
            .downloadAuthenticated(info.path)
            .let {
                Log.d(LOG_TAG, "success to resolve file path: $path")
                it
            }
    }

    override fun resolveFileAsFlow(path: String): Flow<_DownloadStatus> {
        if (!checkPath(path)) return flowOf(_DownloadStatus.Error("fail to resolve file path: $path"))

        val info = getFileInfo(path)

        return supabaseClient
            .storage[info.bucket]
            .downloadAuthenticatedAsFlow(info.path)
            .map {
                when (it) {
                    is DownloadStatus.Progress -> {
                        val progress = it.totalBytesReceived.toFloat() / it.contentLength
                        Log.d(LOG_TAG, "progress to resolve file path: $path, progress: $progress")
                        _DownloadStatus.Progress(progress)
                    }
                    is DownloadStatus.Success -> _DownloadStatus.Progress(1f)
                    is DownloadStatus.ByteData -> {
                        Log.d(LOG_TAG, "success to resolve file path: $path")
                        _DownloadStatus.Complete(
                            bytes = it.data
                        )
                    }
                }
            }
    }

    data class FileInfo(
        val bucket: String,
        val path: String
    )

    // expose for testing
    internal fun getFileInfo(path: String): FileInfo = FileInfo(
        bucket = path.substringBefore('/'),
        path = path.substringAfter('/')
    )

    private fun createBucketRegex(bucket: Bucket) = Regex("""
        ^$bucket/${bucket.fixedFolders.takeIf { 
            it.isNotEmpty() 
        }?.joinOr()?.wrapInParentheses() 
            ?: ".*"}/.*\.${bucket.allowedExtensions.joinOr().wrapInParentheses()}$
    """.trimIndent())

    private fun String.wrapInParentheses() = "($this)"

    private fun List<String>.joinOr() = joinToString("|")

    private fun checkPath(path: String): Boolean {
        val regex = Regex("($iconBucketRegex|$chatMediaBucketRegex)")
        return if (!regex.matches(path)) {
            Log.w(LOG_TAG, "fail to resolve file path: $path")
            false
        } else true
    }
}