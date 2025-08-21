package nrr.konnekt.core.domain.repository

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus

typealias MessageResult<T> = Result<T, MessageRepository.MessageError>

/**
 * Contract for message repository.
 */
interface MessageRepository {
    /**
     * Observe messages in a chat, should be ordered based on [Message.sentAt].
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
     * @param attachments The attachments of the message.
     * @return The sent message.
     */
    suspend fun sendMessage(
        chatId: String,
        content: String,
        attachments: List<FileUpload>? = null
    ): MessageResult<Message>

    /**
     * Edit a message.
     *
     * @param messageId The ID of the message to edit.
     * @param newContent The new content of the message.
     * @return The edited message.
     */
    suspend fun editMessage(messageId: String, newContent: String): MessageResult<Message>

    /**
     * Delete a message by setting [Message.isHidden] to true.
     *
     * @param messageId The ID of the message to delete.
     * @return The deleted message.
     */
    suspend fun deleteMessage(messageId: String): MessageResult<Message>

    /**
     * Mark a message as read.
     *
     * @param messageId The ID of the message to mark as read.
     * @return The updated message status.
     */
    suspend fun markMessageAsRead(messageId: String): MessageResult<MessageStatus>

    /**
     * Hide a message for the logged in user.
     *
     * @param messageId The ID of the message to hide.
     * @return The updated message status.
     */
    suspend fun hideMessage(messageId: String): MessageResult<MessageStatus>

    sealed interface MessageError : Error {
        object ChatNotFound : MessageError
        object MessageNotFound : MessageError
        object FileUploadError : MessageError
        data class DisallowedFileType(val fileTypes: List<String>) : MessageError
        object Unknown : MessageError
    }
}