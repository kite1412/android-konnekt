package nrr.konnekt.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nrr.konnekt.core.domain.FileCache
import java.io.File
import javax.inject.Inject

internal class CacheDirFileCache @Inject constructor(
    @param:ApplicationContext private val context: Context
) : FileCache {
    private val cacheDir = context.cacheDir

    override suspend fun get(key: String): ByteArray? = ioContext {
        val file = getFile(key)
        if (file.exists()) file.readBytes() else null
    }

    override suspend fun set(key: String, data: ByteArray) = ioContext {
        val file = getFile(key)
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        file.outputStream().use { it.write(data) }
    }

    override suspend fun contains(key: String): Boolean =
        getFile(key).exists()

    override suspend fun remove(key: String): Boolean = ioContext {
        getFile(key).delete()
    }

    private fun getFile(path: String) = File(cacheDir, path)

    private suspend fun <T> ioContext(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO, block)
}