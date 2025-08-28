package nrr.konnekt.core.network.supabase.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.FileResolver
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.annotation.DelegateResolver
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.MessageRepository
import nrr.konnekt.core.domain.repository.UserRepository
import nrr.konnekt.core.network.supabase.SupabaseAuthentication
import nrr.konnekt.core.network.supabase.SupabaseChatRepository
import nrr.konnekt.core.network.supabase.SupabaseFileResolver
import nrr.konnekt.core.network.supabase.SupabaseMessageRepository
import nrr.konnekt.core.network.supabase.SupabaseUserPresenceManager
import nrr.konnekt.core.network.supabase.SupabaseUserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SupabaseModule {
    @Binds
    @Singleton
    fun bindAuthentication(
        supabaseAuthentication: SupabaseAuthentication
    ): Authentication

    @Binds
    fun bindChatRepository(
        supabaseChatRepository: SupabaseChatRepository
    ): ChatRepository

    @Binds
    fun bindUserRepository(
        supabaseUserRepository: SupabaseUserRepository
    ): UserRepository

    @Binds
    fun bindMessageRepository(
        supabaseMessageRepository: SupabaseMessageRepository
    ): MessageRepository

    @Binds
    @Singleton
    fun bindUserPresenceManager(
        supabaseUserPresenceManager: SupabaseUserPresenceManager
    ): UserPresenceManager

    @Binds
    @DelegateResolver
    fun bindSupabaseFileResolver(
        supabaseFileResolver: SupabaseFileResolver
    ): FileResolver
}