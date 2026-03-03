package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.UserMessageStatus
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateAttachment
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment
import nrr.konnekt.core.network.supabase.dto.response.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUserMessageStatus
import nrr.konnekt.core.network.supabase.dto.response.toAttachment
import nrr.konnekt.core.network.supabase.dto.response.toMessage
import nrr.konnekt.core.network.supabase.dto.response.toModel
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import nrr.konnekt.core.network.supabase.util.resolveFileType
import javax.inject.Inject

internal class SupabaseMessageRepository @Inject constructor(
    authentication: Authentication,
    private val fileNameFormatter: SupabaseFileNameFormatter,
    private val fileUploadConstraints: SupabaseFileUploadConstraints
) : MessageRepository, SupabaseService(authentication) {
    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    override fun observeMessages(chatId: String): Flow<List<Message>> =
        performOperation(MESSAGES) { user ->
            selectAsFlow(
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
                                SupabaseUser::id isIn m.map { it.senderId }
                            }
                        }
                            .decodeList<SupabaseUser>()
                            .map(SupabaseUser::toModel)
                    }
                    val attachments = attachments {
                        select {
                            filter {
                                SupabaseAttachment::chatId eq chatId
                            }
                        }
                            .decodeList<SupabaseAttachment>()
                    }
                    // TODO make it observable
                    // only get statuses with isDeleted == true
                    val statuses = userMessageStatuses {
                        select {
                            filter {
                                SupabaseUserMessageStatus::isDeleted eq true
                                SupabaseUserMessageStatus::userId eq user.id
                                SupabaseUserMessageStatus::messageId isIn m.map(SupabaseMessage::id)
                            }
                        }
                            .decodeList<SupabaseUserMessageStatus>()
                    }
                    val usersWithStatus = users {
                        select {
                            filter {
                                SupabaseUser::id isIn statuses.map(SupabaseUserMessageStatus::userId)
                            }
                        }
                            .decodeList<SupabaseUser>()
                    }

                    m
                        .mapNotNull {
                            senders
                                .firstOrNull { s -> s.id == it.senderId }
                                ?.let(it::toMessage)
                                ?.copy(
                                    messageStatuses = statuses
                                        .filter { statuses ->
                                            statuses.messageId == it.id
                                        }
                                        .map { status ->
                                            status.toModel(
                                                user = usersWithStatus
                                                    .first { user ->
                                                        user.id == status.userId
                                                    }
                                                    .toModel()
                                            )
                                        },
                                    attachments = attachments
                                        .filter { m ->
                                            m.messageId == it.id
                                        }
                                        .map(SupabaseAttachment::toAttachment)
                                )
                        }
                        .sortedByDescending { it.sentAt }
                }
        }

    override suspend fun sendMessage(
        chatId: String,
        content: String,
        attachments: List<FileUpload>?
    ): MessageResult<Message> = performSuspendingAuthenticatedAction result@{ u ->
        val createAttachments = mutableListOf<SupabaseCreateAttachment>()

        if (!attachments.isNullOrEmpty()) {
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
                            val fileName = fileNameFormatter.format(
                                rawName = it.fileName,
                                ext = it.fileExtension
                            )
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
                                    type = resolveFileType(
                                        fileExtension = it.fileExtension,
                                        fileUploadConstraints = fileUploadConstraints
                                    ),
                                    path = path.fullPath,
                                    name = fileName,
                                    size = it.size
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
            .also {
                Bucket.CHAT_MEDIA.perform {
                    try {
                        delete(
                            paths = createAttachments.map { a -> a.path }
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    override suspend fun editMessage(
        messageId: String,
        newContent: String
    ): MessageResult<Message> = performSuspendingAuthenticatedAction { user ->
        messages {
            update(
                update = {
                    SupabaseMessage::content setTo newContent
                    SupabaseMessage::editedAt setTo now()
                }
            ) {
                filter {
                    SupabaseMessage::id eq messageId
                }
                select()
            }
        }
            .decodeSingleOrNull<SupabaseMessage>()
            ?.toMessage(user)
            ?.let(Result<Message, Nothing>::Success)
            ?: Result.Error(MessageError.Unknown)
    }

    override suspend fun deleteMessages(messageIds: List<String>): MessageResult<List<Message>> =
        performSuspendingAuthenticatedAction { u ->
            messages {
                update(
                    update = {
                        SupabaseMessage::isHidden setTo true
                    }
                ) {
                    filter {
                        SupabaseMessage::id isIn messageIds
                    }
                    select()
                }
            }
                .decodeList<SupabaseMessage>()
                .takeIf { list -> list.isNotEmpty() }?.let { list ->
                    Success(
                        data = list.map { raw ->
                            raw.toMessage(u)
                        }
                    )
                } ?: Error(MessageError.Unknown)
        }
    
    override suspend fun hideMessages(messageIds: List<String>): MessageResult<List<UserMessageStatus>> =
        performSuspendingAuthenticatedAction { user ->
            userMessageStatuses {
                upsert(
                    values = messageIds.map { messageId ->
                        SupabaseUserMessageStatus(
                            userId = user.id,
                            messageId = messageId,
                            isDeleted = true
                        )
                    }
                ) {
                    select()
                }
            }
                .decodeList<SupabaseUserMessageStatus>()
                .takeIf { l -> l.isNotEmpty() }
                ?.map {
                    it.toModel(user)
                }
                ?.let(Result<List<UserMessageStatus>, Nothing>::Success)
                ?: Result.Error(MessageError.Unknown)
        }
}