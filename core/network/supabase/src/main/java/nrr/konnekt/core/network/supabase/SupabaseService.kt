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
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGE_STATUSES
import nrr.konnekt.core.network.supabase.util.Tables.USERS
import nrr.konnekt.core.network.supabase.util.Tables.USER_STATUSES

internal abstract class SupabaseService(
    private val authentication: Authentication
) {
    protected suspend fun <R> performSuspendingAuthenticatedAction(action: suspend (User) -> R) =
        with(authentication.loggedInUser.first()) {
            if (this != null) action(this)
            else throw UnauthenticatedException()
        }

    protected fun <R> performAuthenticatedAction(action: (User) -> R) =
        authentication.getLoggedInUserOrNull()?.let(action)
            ?: throw UnauthenticatedException()

    protected suspend fun <R> performSuspendingOperation(
        tableName: String,
        operation: suspend PostgrestQueryBuilder.() -> R
    ) = performSuspendingAuthenticatedAction {
        operation(supabaseClient.postgrest[tableName])
    }

    protected fun <R> performOperation(
        tableName: String,
        operation: PostgrestQueryBuilder.() -> R
    ) = performAuthenticatedAction {
        operation(supabaseClient.postgrest[tableName])
    }

    protected suspend fun <R> users(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(USERS, operation)

    protected suspend fun <R> chats(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(CHATS, operation)

    protected suspend fun <R> chatParticipants(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(CHAT_PARTICIPANTS, operation)

    protected suspend fun <R> chatSettings(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(CHAT_SETTINGS, operation)

    protected suspend fun <R> chatPermissionSettings(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(CHAT_PERMISSION_SETTINGS, operation)

    protected suspend fun <R> messages(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(MESSAGES, operation)

    protected suspend fun <R> messageStatuses(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(MESSAGE_STATUSES, operation)

    protected suspend fun <R> userStatuses(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(USER_STATUSES, operation)
}