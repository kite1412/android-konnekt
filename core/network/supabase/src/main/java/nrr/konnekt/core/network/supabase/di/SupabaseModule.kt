package nrr.konnekt.core.network.supabase.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.network.api.Authentication
import nrr.konnekt.core.network.supabase.SupabaseAuthentication

@Module
@InstallIn(SingletonComponent::class)
internal interface SupabaseModule {
    @Binds
    fun bindAuthentication(
        supabaseAuthentication: SupabaseAuthentication
    ): Authentication
}