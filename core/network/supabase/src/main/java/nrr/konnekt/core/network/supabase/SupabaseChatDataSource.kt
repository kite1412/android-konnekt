package nrr.konnekt.core.network.supabase

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.model.ChatDetail
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Event

class SupabaseChatDataSource : ChatRepository {
    override fun observeLatestChatMessages(): Flow<List<LatestChatMessage>> {
        TODO("Not yet implemented")
    }

    override fun observeActiveParticipants(chatId: String): Flow<List<ChatParticipant>> {
        TODO("Not yet implemented")
    }

    override suspend fun getJoinedChats(userId: String): List<Chat> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatDetail(chatId: String): ChatDetail? {
        TODO("Not yet implemented")
    }

    override suspend fun joinChat(chatId: String): ChatParticipant? {
        TODO("Not yet implemented")
    }

    override suspend fun leaveChat(chatId: String): ChatParticipant? {
        TODO("Not yet implemented")
    }

    override suspend fun createChat(
        type: ChatType,
        chatSetting: ChatSetting?,
        participantsId: List<String>?
    ): Chat? {
        TODO("Not yet implemented")
    }

    override suspend fun createEvent(
        chatId: String,
        title: String,
        description: String?,
        startsAt: Long
    ): Event? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(eventId: String): Event? {
        TODO("Not yet implemented")
    }

    override suspend fun editEvent(
        eventId: String,
        title: String,
        description: String?,
        startsAt: Long
    ): Event? {
        TODO("Not yet implemented")
    }
}