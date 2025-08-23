package nrr.konnekt.core.domain

class CachingFileResolver(
     private val delegate: FileResolver
) : FileResolver {
    private val cache = mutableMapOf<String, ByteArray>()

    override suspend fun resolveFile(path: String): ByteArray? {
        if (path in cache) return cache[path]
        val bytes = delegate.resolveFile(path)
        return bytes?.let {
            cache[path] = it
            it
        }
    }
}