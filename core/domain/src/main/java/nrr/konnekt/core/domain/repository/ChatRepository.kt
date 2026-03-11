package nrr.konnekt.core.domain.repository

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.exception.UnauthenticatedException
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.model.UpdateChatParticipantStatus
import nrr.konnekt.core.domain.model.UserChatParticipation
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.model.ChatType

typealias ChatResult<T> = Result<T, ChatRepository.ChatError>

/**
 * Contract for chat repository.
 *
 * All methods reflect user actions and may throw [UnauthenticatedException]
 * if the user is not authenticated.
 */
interface ChatRepository {
    /**
     * Observe the latest message in each chat the logged-in user joined to,
     * and listen to their changes.
     *
     * @return A flow of the latest chat messages.
     */
    fun observeLatestChatMessages(): Flow<List<LatestChatMessage>>

    /**
     * Observe active participants in a chat.
     *
     * @param chatId The ID of the chat to observe active participants for.
     * @return A flow of active participants in the chat.
     */
    fun observeActiveParticipants(chatId: String): Flow<List<ChatParticipant>>

    /**
     * Observe chat participants for a chat.
     *
     * @param chatId The ID of the chat to get the chat participants for.
     * @return The chat participants.
     */
    fun observeChatParticipants(chatId: String): Flow<List<ChatParticipant>>

    /**
     * Observe the current user's chat participation in a chat.
     *
     * @param chatId The ID of the chat to get the current user's chat participation for.
     * @return The current user's chat participation.
     */
    fun observeCurrentUserChatParticipation(chatId: String): Flow<UserChatParticipation?>

    /**
     * Observes all chat participation of the current user.
     *
     * @return A flow emitting the list of Chat and ChatParticipant entries for the current user.
     */
    fun observeCurrentUserChatParticipations(): Flow<List<UserChatParticipation>>

    /**
     * Get a chat by its ID.
     *
     * @param chatId The ID of the chat to get.
     * @return The chat with the given ID.
     */
    suspend fun getChatById(chatId: String): ChatResult<Chat>

    /**
     * Get a list of chats the user is joined to.
     *
     * @param userId The ID of the user to get joined chats for.
     * @return A list of chats the user is subscribed to.
     */
    suspend fun getJoinedChats(userId: String, type: ChatType? = null): ChatResult<List<Chat>>

    /**
     * Get a list of chat participants given a chat ID.
     *
     * @param chatId The ID of the chat to get participants for.
     * @return A list of chat participants.
     */
    suspend fun getChatParticipants(chatId: String): ChatResult<List<ChatParticipant>>

    /**
     * Get the current user's chat invitations from other users.
     *
     * @return A list of chat invitations.
     */
    suspend fun getChatInvitations(): ChatResult<List<ChatInvitation>>

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
     * @return Participant of the left chat with [ChatParticipantStatus.leftAt] set.
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
     * Invite users to a chat.
     *
     * @param chatId The ID of the chat to invite users to.
     * @param receiverIds The IDs of the users to invite.
     * @return The list of chat invitations.
     */
    suspend fun inviteToChat(
        chatId: String,
        receiverIds: List<String>
    ): ChatResult<List<ChatInvitation>>

    /**
     * Cancel chat invitations
     *
     * @param inviterId The ID of the user who is inviting the users.
     * @param chatId The ID of the chat to cancel invitations for.
     * @param receiverIds The IDs of the users to cancel invitations for.
     * @return Whether the invitations were canceled.
     */
    suspend fun cancelChatInvitations(
        inviterId: String,
        chatId: String,
        receiverIds: List<String>
    ): ChatResult<Boolean>

    /**
     * Update current user chat participant status.
     *
     * @param update The update payload.
     * @return The updated user read marker.
     */
    suspend fun updateCurrentUserChatParticipantStatus(update: UpdateChatParticipantStatus): ChatResult<ChatParticipantStatus>

    sealed interface ChatError : Error {
        object ChatNotFound : ChatError
        object ParticipantLimitViolation: ChatError
        object ChatSettingNotFound : ChatError
        object FileUploadError : ChatError
        object ParticipantRoleViolation : ChatError
        object Unknown : ChatError
    }
}