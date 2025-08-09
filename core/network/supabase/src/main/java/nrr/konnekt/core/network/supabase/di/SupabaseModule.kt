package nrr.konnekt.core.network.supabase.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.ImagePathResolver
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.annotation.DelegateResolver
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.network.supabase.SupabaseAuthentication
import nrr.konnekt.core.network.supabase.SupabaseChatRepository
import nrr.konnekt.core.network.supabase.SupabaseImageResolver
import nrr.konnekt.core.network.supabase.SupabaseUserPresenceManager

@Module
@InstallIn(SingletonComponent::class)
internal interface SupabaseModule {
    @Binds
    fun bindAuthentication(
        supabaseAuthentication: SupabaseAuthentication
    ): Authentication

    @Binds
    fun bindChatRepository(
        supabaseChatRepository: SupabaseChatRepository
    ): ChatRepository

    @Binds
    fun bindUserPresenceManager(
        supabaseUserPresenceManager: SupabaseUserPresenceManager
    ): UserPresenceManager

    @Binds
    @DelegateResolver
    fun bindImageResolver(
        supabaseImageResolver: SupabaseImageResolver
    ): ImagePathResolver
}