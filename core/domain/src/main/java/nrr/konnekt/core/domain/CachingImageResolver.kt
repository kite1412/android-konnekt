package nrr.konnekt.core.domain

import nrr.konnekt.core.domain.annotation.DelegateResolver
import javax.inject.Inject

internal class CachingImageResolver @Inject constructor(
     @DelegateResolver private val delegate: ImagePathResolver
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