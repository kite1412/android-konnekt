package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.repository.MessageRepository
import nrr.konnekt.core.domain.repository.MessageRepository.MessageError
import nrr.konnekt.core.domain.repository.MessageResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.supabase.dto.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateAttachment
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment
import nrr.konnekt.core.network.supabase.dto.response.toAttachment
import nrr.konnekt.core.network.supabase.dto.toMessage
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.Tables.ATTACHMENTS
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import nrr.konnekt.core.network.supabase.util.resolveFileType
import javax.inject.Inject

internal class SupabaseMessageRepository @Inject constructor(
    authentication: Authentication
) : MessageRepository, SupabaseService(authentication) {
    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    override fun observeMessages(chatId: String): Flow<List<Message>> =
        performOperation(MESSAGES) {
            val messages = selectAsFlow(
                primaryKey = SupabaseMessage::id,
                filter = FilterOperation(
                    column = "chat_id",
                    operator = FilterOperator.EQ,
                    value = chatId
                )
            )
                .map { m ->
                    val senders = users {
                        select {
                            filter {
                                User::id isIn m.map { it.senderId }
                            }
                        }
                            .decodeList<User>()
                    }

                    m.mapNotNull {
                        senders
                            .firstOrNull { s -> s.id == it.senderId }
                            ?.let(it::toMessage)
                    }
                }
            val attachments = performOperation(ATTACHMENTS) {
                selectAsFlow(
                    primaryKey = SupabaseAttachment::id,
                    filter = FilterOperation(
                        column = "chat_id",
                        operator = FilterOperator.EQ,
                        value = chatId
                    )
                )
            }

            combine(
                flow = messages,
                flow2 = attachments
            ) { messages, attachments ->
                messages.map {
                    it.copy(
                        attachments = attachments
                            .filter { a -> a.messageId == it.id }
                            .map(SupabaseAttachment::toAttachment)
                    )
                }
            }
        }

    override suspend fun sendMessage(
        chatId: String,
        content: String,
        attachments: List<FileUpload>?
    ): MessageResult<Message> = performSuspendingAuthenticatedAction result@{ u ->
        val createAttachments = mutableListOf<SupabaseCreateAttachment>()

        if (attachments != null && attachments.isNotEmpty()) {
            with(Bucket.CHAT_MEDIA) {
                val (allowed, disallowed) = attachments.partition {
                    allowedExtensions.contains(it.fileExtension)
                }
                if (disallowed.isNotEmpty()) {
                    return@result Error(
                        MessageError.DisallowedFileType(
                            disallowed.map { it.fileExtension }
                        )
                    )
                }
                try {
                    perform {
                        allowed.forEach {
                            val fileName = "${now()}_${it.fileName}${
                                if (it.fileName.substringAfterLast('.') != it.fileExtension)
                                    ".${it.fileExtension}"
                                else ""
                            }"
                            val path = createPath(
                                fileName = fileName,
                                rootFolder = chatId
                            )
                            upload(
                                path = path.pathInBucket,
                                data = it.content
                            ) {
                                userMetadata = buildJsonObject {
                                    put("user_id", u.id)
                                    put("email", u.email)
                                }
                            }
                            createAttachments.add(
                                SupabaseCreateAttachment(
                                    type = resolveFileType(it.fileExtension),
                                    path = path.fullPath,
                                    name = it.fileName
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@result Error(MessageError.FileUploadError)
                }
            }
        }

        rpc.sendMessageWithAttachments(
            chatId = chatId,
            content = content,
            attachments = createAttachments
        )
            ?.let {
                Success(
                    it.message.toMessage(u).copy(
                        attachments = it.attachments
                            .map(SupabaseAttachment::toAttachment)
                    )
                )
            } ?: Error(MessageError.Unknown)
    }

    override suspend fun editMessage(
        messageId: String,
        newContent: String
    ): MessageResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(messageId: String): MessageResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun markMessageAsRead(messageId: String): MessageResult<MessageStatus> {
        TODO("Not yet implemented")
    }

    override suspend fun hideMessage(messageId: String): MessageResult<MessageStatus> {
        TODO("Not yet implemented")
    }
}