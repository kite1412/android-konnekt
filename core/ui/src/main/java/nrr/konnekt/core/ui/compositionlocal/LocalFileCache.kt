package nrr.konnekt.core.ui.compositionlocal

import androidx.compose.runtime.compositionLocalOf
import nrr.konnekt.core.domain.FileCache

val LocalFileCache = compositionLocalOf<FileCache> {
    object : FileCache {
        override suspend fun get(key: String): ByteArray? = null

        override suspend fun set(key: String, data: ByteArray) {}

        override suspend fun contains(key: String): Boolean = false

        override suspend fun remove(key: String): Boolean = false
    }
}