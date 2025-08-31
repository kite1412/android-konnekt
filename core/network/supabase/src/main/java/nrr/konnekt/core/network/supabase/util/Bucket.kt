package nrr.konnekt.core.network.supabase.util

import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import nrr.konnekt.core.model.util.AllowedFileType.audioTypes
import nrr.konnekt.core.model.util.AllowedFileType.documentTypes
import nrr.konnekt.core.model.util.AllowedFileType.imageTypes
import nrr.konnekt.core.model.util.AllowedFileType.videoTypes
import nrr.konnekt.core.network.supabase.supabaseClient

internal enum class Bucket(
    val fixedFolders: List<String>,
    val allowedExtensions: List<String>
) {
    ICON(
        fixedFolders = listOf("person", "group"),
        allowedExtensions = imageTypes.map { it.extension }
    ),
    CHAT_MEDIA(
        fixedFolders = emptyList(),
        allowedExtensions = listOf(
            *imageTypes.toTypedArray(),
            *videoTypes.toTypedArray(),
            *audioTypes.toTypedArray(),
            *documentTypes.toTypedArray()
        ).map { it.extension }
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