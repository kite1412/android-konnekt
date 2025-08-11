package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.storage.storage
import nrr.konnekt.core.domain.ImagePathResolver
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import javax.inject.Inject

internal class SupabaseImageResolver @Inject constructor()
    : ImagePathResolver {

    // expose for testing
    internal val iconBucketRegex = createBucketRegex(Bucket.ICON)
    internal val chatMediaBucketRegex = createBucketRegex(Bucket.CHAT_MEDIA)

    override suspend fun resolveImage(path: String): ByteArray? {
        val regex = Regex("($iconBucketRegex|$chatMediaBucketRegex)")
        if (!regex.matches(path)) {
            Log.w(LOG_TAG, "fail to resolve image path: $path")
            return null
        }
        val info = getImageInfo(path)
        return supabaseClient
            .storage[info.bucket]
            .downloadAuthenticated(info.path)
            .let {
                Log.d(LOG_TAG, "success to resolve image path: $path")
                it
            }
    }

    data class ImageInfo(
        val bucket: String,
        val path: String
    )

    // expose for testing
    internal fun getImageInfo(path: String): ImageInfo = ImageInfo(
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