package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.repository.MessageRepository
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.network.supabase.dto.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.toMessage
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import javax.inject.Inject

internal class SupabaseMessageRepository @Inject constructor(
    authentication: Authentication
) : MessageRepository, SupabaseService(authentication) {
    @OptIn(SupabaseExperimental::class)
    override fun observeMessages(chatId: String): Flow<List<Message>> =
        performOperation(MESSAGES) {
            selectAsFlow(
                primaryKey = SupabaseMessage::id,
                filter = FilterOperation(
                    column = "chat_id",
                    operator = FilterOperator.EQ,
                    value = chatId
                )
            )
                .map {
                    it.map(SupabaseMessage::toMessage)
                }
        }

    override suspend fun sendMessage(
        chatId: String,
        content: String,
        attachments: List<ByteArray>?
    ): Message? {
        TODO("Not yet implemented")
    }

    override suspend fun editMessage(
        messageId: String,
        newContent: String
    ): Message? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(messageId: String): Message? {
        TODO("Not yet implemented")
    }

    override suspend fun markMessageAsRead(messageId: String): MessageStatus? {
        TODO("Not yet implemented")
    }

    override suspend fun hideMessage(messageId: String): MessageStatus? {
        TODO("Not yet implemented")
    }
}