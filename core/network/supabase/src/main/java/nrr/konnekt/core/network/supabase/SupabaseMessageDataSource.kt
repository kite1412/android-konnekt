package nrr.konnekt.core.network.supabase

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.network.api.MessageNetworkDataSource

class SupabaseMessageDataSource : MessageNetworkDataSource {
    override fun observeMessages(chatId: String): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(
        chatId: String,
        content: String
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