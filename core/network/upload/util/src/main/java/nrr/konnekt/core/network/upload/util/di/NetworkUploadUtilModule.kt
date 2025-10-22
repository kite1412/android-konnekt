package nrr.konnekt.core.network.upload.util.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import nrr.konnekt.core.network.upload.domain.FileResolver
import nrr.konnekt.core.network.upload.util.CachingFileResolver
import nrr.konnekt.core.network.upload.util.annotation.DelegateResolver
import nrr.konnekt.core.storage.file.FileCache

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkUploadUtilModule {
    @Provides
    @Singleton
    fun provideCachingFileResolver(
        @DelegateResolver delegate: FileResolver,
        fileCache: FileCache
    ): FileResolver = CachingFileResolver(delegate, fileCache)
}