package nrr.konnekt.core.domain.util

import nrr.konnekt.core.domain.FileCache

class MapFileCache : FileCache {
    private val map = mutableMapOf<String, ByteArray>()

    override fun get(key: String): ByteArray? = map[key]

    override fun set(key: String, data: ByteArray) {
        map[key] = data
    }

    override fun contains(key: String): Boolean = key in map

    override fun remove(key: String): Boolean =
        map.remove(key) != null
}