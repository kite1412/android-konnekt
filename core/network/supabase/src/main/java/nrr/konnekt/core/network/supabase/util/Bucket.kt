package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.network.supabase.util.MediaType.allowedAudioTypes
import nrr.konnekt.core.network.supabase.util.MediaType.allowedImageTypes
import nrr.konnekt.core.network.supabase.util.MediaType.allowedVideoTypes

internal enum class Bucket(
    val fixedFolders: List<String>,
    val allowedExtensions: List<String>
) {
    ICON(
        fixedFolders = listOf("person", "group"),
        allowedExtensions = allowedImageTypes
    ),
    CHAT_MEDIA(
        fixedFolders = emptyList(),
        allowedExtensions = listOf(
            *allowedImageTypes.toTypedArray(),
            *allowedVideoTypes.toTypedArray(),
            *allowedAudioTypes.toTypedArray()
        )
    );

    override fun toString(): String =
        this.name.lowercase().run {
            replace("_", "-")
        }
}