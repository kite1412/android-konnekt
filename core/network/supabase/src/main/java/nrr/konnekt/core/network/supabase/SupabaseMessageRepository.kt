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
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserReadMarker
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.supabase.dto.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.SupabaseUserReadMarker
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateAttachment
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment
import nrr.konnekt.core.network.supabase.dto.response.toAttachment
import nrr.konnekt.core.network.supabase.dto.toMessage
import nrr.konnekt.core.network.supabase.dto.toUserReadMarker
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.Tables.USER_READ_MARKERS
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import nrr.konnekt.core.network.supabase.util.resolveFileType
import javax.inject.Inject
import kotlin.time.Instant

internal class SupabaseMessageRepository @Inject constructor(
    authentication: Authentication,
    private val fileNameFormatter: SupabaseFileNameFormatter
) : MessageRepository, SupabaseService(authentication) {
    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
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
                .map { m ->
                    val senders = users {
                        select {
                            filter {
                                User::id isIn m.map { it.senderId }
                            }
                        }
                            .decodeList<User>()
                    }
                    val attachments = attachments {
                        select {
                            filter {
                                SupabaseAttachment::chatId eq chatId
                            }
                        }
                            .decodeList<SupabaseAttachment>()
                    }

                    m
                        .mapNotNull {
                            senders
                                .firstOrNull { s -> s.id == it.senderId }
                                ?.let(it::toMessage)
                                ?.copy(
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

    @OptIn(SupabaseExperimental::class)
    override fun observeUserReadMarkers(chatId: String): Flow<List<UserReadMarker>> =
        performAuthenticatedAction { u ->
            performOperation(USER_READ_MARKERS) {
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseUserReadMarker::userId,
                        SupabaseUserReadMarker::chatId
                    ),
                    filter = FilterOperation(
                        column = "chat_id",
                        operator = FilterOperator.EQ,
                        value = chatId
                    )
                )
                    .map {
                        val filtered = it.filter { m -> m.userId != u.id }
                        val markers = mutableListOf<UserReadMarker>()
                        filtered
                            .chunked(10)
                            .forEach { l ->
                                val users = users {
                                    select {
                                        filter {
                                            User::id isIn l.map { m -> m.userId }
                                        }
                                    }
                                        .decodeList<User>()
                                }
                                markers.addAll(
                                    users.mapNotNull { u ->
                                        l.firstOrNull { m -> m.userId == u.id }
                                            ?.lastReadAt
                                            ?.let { lastReadAt ->
                                                UserReadMarker(
                                                    user = u,
                                                    chatId = chatId,
                                                    lastReadAt = lastReadAt
                                                )
                                            }
                                    }
                                )
                            }

                        markers
                    }
            }
        }

    @OptIn(SupabaseExperimental::class)
    override fun observeCurrentUserReadMarkers(): Flow<List<UserReadMarker>> =
        performAuthenticatedAction {  u ->
            performOperation(USER_READ_MARKERS) {
                selectAsFlow(
                    primaryKey = SupabaseUserReadMarker::chatId,
                    filter = FilterOperation(
                        column = "user_id",
                        operator = FilterOperator.EQ,
                        value = u.id
                    )
                )
                    .map {
                        it.map { m ->
                            m.toUserReadMarker(u)
                        }
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
                            val fileName = fileNameFormatter.format(it.fileName)
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
                                    name = fileName
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
    ): MessageResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(messageId: String): MessageResult<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserReadMarker(
        chatId: String,
        instant: Instant?
    ): MessageResult<UserReadMarker> =
        performSuspendingAuthenticatedAction { u ->
            userReadMarkers {
                upsert(
                    value = SupabaseUserReadMarker(
                        userId = u.id,
                        chatId = chatId,
                        lastReadAt = instant ?: now()
                    )
                ) {
                    select()
                }
            }
                .decodeSingleOrNull<SupabaseUserReadMarker>()
                ?.let {
                    Success(
                        UserReadMarker(
                            user = u,
                            chatId = chatId,
                            lastReadAt = it.lastReadAt
                        )
                    )
                } ?: Error(MessageError.Unknown)
        }

    override suspend fun hideMessage(messageId: String): MessageResult<MessageStatus> {
        TODO("Not yet implemented")
    }
}