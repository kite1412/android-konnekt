package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import kotlinx.coroutines.flow.first
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.exception.UnauthenticatedException
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.util.Tables.CHATS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PARTICIPANTS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PERMISSION_SETTINGS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_SETTINGS
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.Tables.USERS

internal abstract class SupabaseRepository(
    private val authentication: Authentication
) {
    suspend fun <R> performAuthenticatedAction(action: suspend (User) -> R) =
        authentication.loggedInUser.first()?.run {
            action(this)
        } ?: throw UnauthenticatedException()

    suspend fun <R> performOperation(
        tableName: String,
        operation: suspend PostgrestQueryBuilder.() -> R?
    ) = operation(supabaseClient.postgrest[tableName])

    suspend fun <R> users(operation: suspend PostgrestQueryBuilder.() -> R?) =
        performOperation(USERS, operation)

    suspend fun <R> chats(operation: suspend PostgrestQueryBuilder.() -> R?) =
        performOperation(CHATS, operation)

    suspend fun <R> chatParticipants(operation: suspend PostgrestQueryBuilder.() -> R?) =
        performOperation(CHAT_PARTICIPANTS, operation)

    suspend fun <R> chatSettings(operation: suspend PostgrestQueryBuilder.() -> R?) =
        performOperation(CHAT_SETTINGS, operation)

    suspend fun <R> chatPermissionSettings(operation: suspend PostgrestQueryBuilder.() -> R?) =
        performOperation(CHAT_PERMISSION_SETTINGS, operation)

    suspend fun <R> messages(operation: suspend PostgrestQueryBuilder.() -> R?) =
        performOperation(MESSAGES, operation)
}