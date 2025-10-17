package nrr.konnekt.core.domain

/**
 * Defines operations for caching and retrieving file contents as [ByteArray] data.
 */
interface FileCache {
    /**
     * Get a cached data for the given [key], or null if not found.
     *
     * @param key Unique identifier used to locate the cached data.
     * @return The cached data, or null if not found.
     */
    operator fun get(key: String): ByteArray?

    /**
     * Caches the given [data] under the specified [key].
     *
     * @param key Unique identifier for the cache entry.
     * @param data Data to be cached.
     * @return True if the data was successfully cached, false otherwise.
     */
    operator fun set(key: String, data: ByteArray)

    /**
     * Checks whether a cache entry with given [key] exists.
     *
     * @param key Unique identifier to find a cache entry with.
     * @return True if the entry exists, false otherwise.
     */
    operator fun contains(key: String): Boolean

    /**
     * Remove cached data for the given [key].
     *
     * @param key Unique identifier of the entry to remove.
     * @return True if the entry was successfully removed, false otherwise.
     */
    fun remove(key: String): Boolean
}