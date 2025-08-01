package nrr.konnekt.core.network.api

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus

/**
 * Contract for network message service.
 */
interface MessageNetworkDataSource {
    /**
     * Observe messages in a chat, should be ordered based on [Message.sentAt],
     * and listen for [Message.isHidden] and [Message.editedAt] **without** altering the message order.
     *
     * @param chatId The ID of the chat to observe messages for.
     * @return A flow of the messages in the chat.
     */
    fun observeMessages(chatId: String): Flow<List<Message>>

    /**
     * Send a message to a chat.
     *
     * @param chatId The ID of the chat to send the message to.
     * @param content The content of the message.
     * @return The sent message.
     */
    suspend fun sendMessage(chatId: String, content: String): Message?

    /**
     * Edit a message.
     *
     * @param messageId The ID of the message to edit.
     * @param newContent The new content of the message.
     * @return The edited message.
     */
    suspend fun editMessage(messageId: String, newContent: String): Message?

    /**
     * Delete a message by setting [Message.isHidden] to true.
     *
     * @param messageId The ID of the message to delete.
     * @return The deleted message.
     */
    suspend fun deleteMessage(messageId: String): Message?

    /**
     * Mark a message as read.
     *
     * @param messageId The ID of the message to mark as read.
     * @return The updated message status.
     */
    suspend fun markMessageAsRead(messageId: String): MessageStatus?

    /**
     * Hide a message for the logged in user.
     *
     * @param messageId The ID of the message to hide.
     * @return The updated message status.
     */
    suspend fun hideMessage(messageId: String): MessageStatus?
}