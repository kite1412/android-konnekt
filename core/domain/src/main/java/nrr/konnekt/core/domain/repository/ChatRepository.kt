package nrr.konnekt.core.domain.repository

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.exception.UnauthenticatedException
import nrr.konnekt.core.domain.model.ChatDetail
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Event
import kotlin.time.Instant

typealias ChatResult<T> = Result<T, ChatRepository.ChatError>

/**
 * Contract for chat repository.
 *
 * All methods reflect user actions and may throw [UnauthenticatedException]
 * if the user is not authenticated.
 */
interface ChatRepository {
    /**
     * Observe the latest message in each chat the logged in user joined to,
     * and listen to their changes.
     *
     * @return A flow of the latest chat messages.
     */
    fun observeLatestChatMessages(): Flow<List<LatestChatMessage>>

    /**
     * Observe the number of active participants in a chat.
     *
     * @param chatId The ID of the chat to observe active participants for.
     * @return A flow of active participants in the chat.
     */
    fun observeActiveParticipants(chatId: String): Flow<List<ChatParticipant>>

    /**
     * Get a list of chats the user is joined to.
     *
     * @param userId The ID of the user to get joined chats for.
     * @return A list of chats the user is subscribed to.
     */
    suspend fun getJoinedChats(userId: String): ChatResult<List<Chat>>

    /**
     * Get a list of chat participants.
     *
     * @param chatId The ID of the chat to get participants for.
     * @return A list of chat participants.
     */
    suspend fun getChatParticipants(chatId: String): ChatResult<List<ChatParticipant>>

    /**
     * Get the detail of a chat.
     *
     * @param chatId The ID of the chat to get the detail for.
     * @return The detail of the chat.
     */
    suspend fun getChatDetail(chatId: String): ChatResult<ChatDetail>

    /**
     * Join a chat.
     *
     * @param chatId The ID of the chat to subscribe to.
     * @return Participant of the joined chat.
     */
    suspend fun joinChat(chatId: String): ChatResult<ChatParticipant>

    /**
     * Leave a chat.
     *
     * @param chatId The ID of the chat to leave.
     * @return Participant of the left chat with [ChatParticipant.leftAt] set.
     */
    suspend fun leaveChat(chatId: String): ChatResult<ChatParticipant>

    /**
     * Create a chat.
     *
     * @param type The type of the chat to create.
     * @param chatSetting The setting of the chat to create.
     * @param participantIds The IDs of the participants to join to the chat, excluding the creator.
     * @return The created chat.
     */
    suspend fun createChat(
        type: ChatType,
        chatSetting: CreateChatSetting? = null,
        participantIds: List<String>? = null
    ): ChatResult<Chat>

    /**
     * Create an event in a chat.
     *
     * @param chatId The ID of the chat to create the event in.
     * @param title The title of the event.
     * @param description The description of the event.
     * @param startsAt The start time of the event.
     * @return The created event.
     */
    suspend fun createEvent(
        chatId: String,
        title: String,
        description: String? = null ,
        startsAt: Instant
    ): ChatResult<Event>

    /**
     * Delete an event
     *
     * @param eventId The ID of the event to delete.
     * @return The deleted event.
     */
    suspend fun deleteEvent(eventId: String): ChatResult<Event>

    /**
     * Edit an event
     *
     * @param eventId The ID of the event to edit.
     * @param title The new title of the event.
     * @param description The new description of the event.
     * @param startsAt The new start time of the event.
     * @return The edited event.
     */
    suspend fun editEvent(
        eventId: String,
        title: String?,
        description: String? = null,
        startsAt: Instant
    ): ChatResult<Event>

    sealed interface ChatError : Error {
        object ChatNotFound : ChatError
        object ParticipantLimitViolation: ChatError
        object ChatSettingNotFound : ChatError
        object Unknown : ChatError
    }
}