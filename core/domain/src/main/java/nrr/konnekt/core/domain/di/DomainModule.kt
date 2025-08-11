package nrr.konnekt.core.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.domain.CachingImageResolver
import nrr.konnekt.core.domain.ImagePathResolver
import nrr.konnekt.core.domain.annotation.DelegateResolver

@Module
@InstallIn(SingletonComponent::class)
internal object DomainModule {
    @Provides
    fun provideCachingImageResolver(
        @DelegateResolver delegate: ImagePathResolver
    ): ImagePathResolver = CachingImageResolver(delegate)
}