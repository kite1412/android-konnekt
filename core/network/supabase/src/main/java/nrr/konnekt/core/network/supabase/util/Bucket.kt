package nrr.konnekt.core.network.supabase.util

import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import nrr.konnekt.core.network.supabase.supabaseClient
import nrr.konnekt.core.network.supabase.util.FileType.allowedAudioExtensions
import nrr.konnekt.core.network.supabase.util.FileType.allowedImageExtensions
import nrr.konnekt.core.network.supabase.util.FileType.allowedVideoExtensions

internal enum class Bucket(
    val fixedFolders: List<String>,
    val allowedExtensions: List<String>
) {
    ICON(
        fixedFolders = listOf("person", "group"),
        allowedExtensions = allowedImageExtensions
    ),
    CHAT_MEDIA(
        fixedFolders = emptyList(),
        allowedExtensions = listOf(
            *allowedImageExtensions.toTypedArray(),
            *allowedVideoExtensions.toTypedArray(),
            *allowedAudioExtensions.toTypedArray()
        )
    );

    override fun toString(): String =
        this.name.lowercase().run {
            replace("_", "-")
        }
}

internal suspend fun <R> Bucket.perform(block: suspend BucketApi.() -> R) =
    block(supabaseClient.storage[this])

/**
 * @param fullPath full path to the file, includes bucket name
 * @param pathInBucket path to the file used in the bucket
 */
internal data class FilePath(
    val fullPath: String,
    val pathInBucket: String
)

internal fun Bucket.createPath(
    fileName: String,
    rootFolder: String? = null
) = with(
    "${
        if (
            fixedFolders.isNotEmpty()
            && (rootFolder == null || rootFolder !in fixedFolders)
        )
            throw IllegalArgumentException("Root folder must be one of: $fixedFolders")
        else rootFolder ?: ""
    }/$fileName"
) {
    FilePath(
        fullPath = "${this@createPath}/$this",
        pathInBucket = this
    )
}

private operator fun Storage.get(bucket: Bucket) = this[bucket.toString()]