package nrr.konnekt.core.domain

/**
 * Contract for resolving files from a given storage path into raw bytes.
 */
fun interface FileResolver {
    /**
     * Resolves a file from a given storage path into raw bytes.
     *
     * @param path The storage path of the file.
     * @return The raw bytes of the file, or null if the file could not be resolved.
     */
    suspend fun resolveFile(path: String): ByteArray?
}