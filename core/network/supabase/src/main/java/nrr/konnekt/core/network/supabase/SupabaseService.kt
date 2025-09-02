package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.exception.UnauthenticatedException
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateAttachment
import nrr.konnekt.core.network.supabase.dto.response.rpc.SendMessageWithAttachments
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import nrr.konnekt.core.network.supabase.util.Tables.ATTACHMENTS
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
    protected val rpc = Rpc()

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

    protected suspend fun <R> attachments(operation: suspend PostgrestQueryBuilder.() -> R) =
        performSuspendingOperation(ATTACHMENTS, operation)

    protected fun List<String>.toInValues(): String =
        joinToString(
            separator = ",",
            prefix = "(",
            postfix = ")"
        )

    protected inner class Rpc {
        private suspend inline fun <reified R : Any> call(
            function: String,
            parameters: JsonObjectBuilder.() -> Unit
        ): R? = try {
            supabaseClient.postgrest.rpc(
                function = function,
                parameters = buildJsonObject(parameters),
            )
                .decodeAsOrNull<R>()
                ?.apply {
                    Log.d(LOG_TAG, "success calling rpc: $function")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        suspend fun sendMessageWithAttachments(
            chatId: String,
            content: String,
            attachments: List<SupabaseCreateAttachment>
        ) = performSuspendingAuthenticatedAction { u ->
            call<SendMessageWithAttachments>(
                function = "send_message_with_attachments",
                parameters = {
                    put("_chat_id", chatId)
                    put("_sender_id", u.id)
                    put("_content", content)
                    if (attachments.isNotEmpty()) put("_attachments", buildJsonArray {
                        attachments.forEach {
                            add(
                                buildJsonObject {
                                    put("type", it.type)
                                    put("path", it.path)
                                    put("name", it.name)
                                    put("size", it.size)
                                }
                            )
                        }
                    })
                }
            )
        }
    }
}