package nrr.konnekt.core.storage.file.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.storage.file.CacheDirFileCache
import nrr.konnekt.core.storage.file.FileCache
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface FileModule {
    @Binds
    @Singleton
    fun bindFCacheDirFileCache(
        cacheDirFileCache: CacheDirFileCache
    ): FileCache
}