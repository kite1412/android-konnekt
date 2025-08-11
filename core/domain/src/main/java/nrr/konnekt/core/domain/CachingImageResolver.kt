package nrr.konnekt.core.domain

class CachingImageResolver(
     private val delegate: ImagePathResolver
) : ImagePathResolver {
    private val cache = mutableMapOf<String, ByteArray>()

    override suspend fun resolveImage(path: String): ByteArray? {
        if (path in cache) return cache[path]
        val bytes = delegate.resolveImage(path)
        return bytes?.let {
            cache[path] = it
            it
        }
    }
}