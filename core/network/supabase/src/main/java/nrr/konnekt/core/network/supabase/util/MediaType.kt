package nrr.konnekt.core.network.supabase.util

internal object MediaType {
    val allowedImageTypes = listOf(
        "jpg",
        "jpeg",
        "png",
        "heif",
        "heic"
    )
    val allowedVideoTypes = listOf(
        "mp4"
    )
    val allowedAudioTypes = listOf(
        "mp3"
    )
}