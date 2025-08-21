package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
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
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.supabase.dto.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateAttachment
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateMessage
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment
import nrr.konnekt.core.network.supabase.dto.response.toAttachment
import nrr.konnekt.core.network.supabase.dto.toMessage
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import nrr.konnekt.core.network.supabase.util.resolveFileType
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

    private data class FileInfo(
        val fileName: String,
        val fullPath: String,
        val extension: String
    )

    override suspend fun sendMessage(
        chatId: String,
        content: String,
        attachments: List<FileUpload>?
    ): MessageResult<Message> = performSuspendingAuthenticatedAction { u ->
        messages result@{
            insert(
                value = SupabaseCreateMessage(
                    chatId = chatId,
                    senderId = u.id,
                    content = content
                )
            ) {
                select()
            }
                .decodeSingleOrNull<SupabaseMessage>()
                ?.let { message ->
                    attachments?.let { uploads ->
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
                                val attachmentPaths = mutableListOf<FileInfo>()

                                perform {
                                    allowed.forEach {
                                        val fileName = "${it.fileName}_${now()}.${it.fileExtension}"
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
                                        attachmentPaths.add(
                                            FileInfo(
                                                fileName = fileName,
                                                fullPath = path.fullPath,
                                                extension = it.fileExtension
                                            )
                                        )
                                    }
                                }
                                val attachments = attachments {
                                    insert(
                                        values = attachmentPaths.map {
                                            SupabaseCreateAttachment(
                                                messageId = message.id,
                                                type = resolveFileType(it.extension),
                                                path = it.fullPath,
                                                name = it.fileName
                                            )
                                        }
                                    ) {
                                        select()
                                    }
                                        .decodeList<SupabaseAttachment>()
                                        .map(SupabaseAttachment::toAttachment)
                                }

                                return@result Success(
                                    data = message.toMessage().copy(
                                        attachments = attachments
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                return@result Error(MessageError.FileUploadError)
                            }
                        }
                    }

                    Success(message.toMessage())
                }
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