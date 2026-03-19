package nrr.konnekt.core.network.upload.util.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.network.upload.domain.FileResolver
import nrr.konnekt.core.network.upload.util.CachingFileResolver

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkUploadUtilModule {
    @Binds
    fun provideCachingFileResolver(
        cachingFileResolver: CachingFileResolver
    ): FileResolver
}