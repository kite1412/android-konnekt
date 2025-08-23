package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.storage.storage
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import javax.inject.Inject

internal class SupabaseFileResolver @Inject constructor()
    : FileResolver {

    // expose for testing
    internal val iconBucketRegex = createBucketRegex(Bucket.ICON)
    internal val chatMediaBucketRegex = createBucketRegex(Bucket.CHAT_MEDIA)

    override suspend fun resolveFile(path: String): ByteArray? {
        val regex = Regex("($iconBucketRegex|$chatMediaBucketRegex)")
        if (!regex.matches(path)) {
            Log.w(LOG_TAG, "fail to resolve file path: $path")
            return null
        }
        val info = getFileInfo(path)
        return supabaseClient
            .storage[info.bucket]
            .downloadAuthenticated(info.path)
            .let {
                Log.d(LOG_TAG, "success to resolve file path: $path")
                it
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
}