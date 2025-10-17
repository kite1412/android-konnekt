package nrr.konnekt.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import nrr.konnekt.core.domain.FileCache
import nrr.konnekt.core.domain.annotation.AppCoroutineScope
import nrr.konnekt.util.CacheDirFileCache
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    @Provides
    @Singleton
    @AppCoroutineScope
    fun provideAppCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideCacheDirFileCache(
        fileCache: CacheDirFileCache
    ): FileCache = fileCache
}