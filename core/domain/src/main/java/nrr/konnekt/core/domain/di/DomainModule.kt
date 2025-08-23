package nrr.konnekt.core.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.domain.CachingFileResolver
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.domain.annotation.DelegateResolver

@Module
@InstallIn(SingletonComponent::class)
internal object DomainModule {
    @Provides
    fun provideCachingFileResolver(
        @DelegateResolver delegate: FileResolver
    ): FileResolver = CachingFileResolver(delegate)
}