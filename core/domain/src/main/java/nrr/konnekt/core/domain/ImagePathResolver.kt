package nrr.konnekt.core.domain

/**
 * Resolves an image from a given storage path into raw bytes.
 */
fun interface ImagePathResolver {
    /**
     * Resolves an image from a given storage path into raw bytes.
     *
     * @param path The storage path of the image.
     * @return The raw bytes of the image, or null if the image could not be resolved.
     */
    suspend fun resolveImage(path: String): ByteArray?
}