package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.model.ChatDetail
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.model.MessageDetail
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.ChatResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Event
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.dto.SupabaseChat
import nrr.konnekt.core.network.supabase.dto.SupabaseChatParticipant
import nrr.konnekt.core.network.supabase.dto.SupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.SupabaseChatSetting
import nrr.konnekt.core.network.supabase.dto.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateChat
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateChatParticipant
import nrr.konnekt.core.network.supabase.dto.response.JoinedChat
import nrr.konnekt.core.network.supabase.dto.toChat
import nrr.konnekt.core.network.supabase.dto.toChatParticipant
import nrr.konnekt.core.network.supabase.dto.toChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.toChatSetting
import nrr.konnekt.core.network.supabase.dto.toMessage
import nrr.konnekt.core.network.supabase.dto.toSupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.toSupabaseChatSetting
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PARTICIPANTS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PERMISSION_SETTINGS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_SETTINGS
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGE_STATUSES
import nrr.konnekt.core.network.supabase.util.Tables.USERS
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import javax.inject.Inject
import kotlin.time.Instant

internal class SupabaseChatRepository @Inject constructor(
    authentication: Authentication,
    private val userPresenceManager: SupabaseUserPresenceManager
) : ChatRepository, SupabaseService(authentication) {
    /*
        TODO:
         - resolve messages' attachments
     */
    @OptIn(
        SupabaseExperimental::class,
        ExperimentalCoroutinesApi::class
    )
    override fun observeLatestChatMessages(): Flow<List<LatestChatMessage>> =
        performAuthenticatedAction { u ->
            val participatedIn = channelFlow {
                performOperation(CHAT_PARTICIPANTS) {
                    selectAsFlow(
                        primaryKeys = listOf(
                            SupabaseChatParticipant::chatId,
                            SupabaseChatParticipant::userId
                        ),
                        filter = FilterOperation(
                            column = "user_id",
                            operator = FilterOperator.EQ,
                            value = u.id
                        )
                    )
                        .map { l ->
                            l
                                .filter { it.leftAt == null }
                                .map {
                                    JoinedChat(
                                        chatId = it.chatId,
                                        userId = it.userId
                                    )
                                }
                        }
                        .onEach {
                            Log.d(LOG_TAG, "joined chats: $it")
                            send(it)
                        }
                        .launchIn(this@channelFlow)
                }
            }
            val toInValues: List<String>.() -> String = {
                joinToString(
                    separator = ",",
                    prefix = "(",
                    postfix = ")"
                )
            }
            val joinedChats = participatedIn.flatMapLatest { jc ->
                flow {
                    emit(
                        chats {
                            select {
                                filter {
                                    SupabaseChat::id isIn jc.map { c -> c.chatId }
                                }
                            }.decodeList<SupabaseChat>()
                        }.map(SupabaseChat::toChat)
                    )
                }
            }
            val supabaseSettings = joinedChats.flatMapLatest { c ->
                performOperation(CHAT_SETTINGS) {
                    selectAsFlow(
                        primaryKey = SupabaseChatSetting::chatId,
                        filter = FilterOperation(
                            column = "chat_id",
                            operator = FilterOperator.IN,
                            value = c.map { it.id }.toInValues()
                        )
                    )
                }
            }
            val supabasePermissionSettings = supabaseSettings.flatMapLatest { s ->
                performOperation(CHAT_PERMISSION_SETTINGS) {
                    selectAsFlow(
                        primaryKey = SupabaseChatPermissionSettings::chatId,
                        filter = FilterOperation(
                            column = "chat_id",
                            operator = FilterOperator.IN,
                            value = s.map { it.chatId }.toInValues()
                        )
                    )
                }
            }
            val settings = combine(
                flow = supabaseSettings,
                flow2 = supabasePermissionSettings
            ) { settings, permissionSettings ->
                settings.map { s ->
                    s.chatId to s.toChatSetting(
                        permissionSettings = permissionSettings.firstOrNull { ps ->
                            ps.chatId == s.chatId
                        }?.toChatPermissionSettings()
                    )
                }
            }
            val otherUsers = joinedChats.flatMapLatest { c ->
                c.filter { it.type == ChatType.PERSONAL }
                    .let { filtered ->
                        chatParticipants {
                            select {
                                filter {
                                    SupabaseChatParticipant::chatId isIn
                                        filtered.map { f -> f.id }
                                    SupabaseChatParticipant::userId neq u.id
                                }
                            }.decodeList<SupabaseChatParticipant>()
                        }.let { p ->
                            if (p.isNotEmpty()) performOperation(USERS) {
                                selectAsFlow(
                                    primaryKey = User::id,
                                    filter = FilterOperation(
                                        column = "id",
                                        operator = FilterOperator.IN,
                                        value = p.map { i -> i.userId }.toInValues()
                                    )
                                )
                                    .map { users ->
                                        users.map { u ->
                                            p.first { it.userId == u.id }.chatId to
                                                u
                                        }
                                    }
                            } else flowOf(emptyList())
                        }
                    }
            }
            val participants = joinedChats.flatMapLatest { chats ->
                flowOf(
                    chatParticipants {
                        select {
                            filter {
                                SupabaseChatParticipant::chatId isIn chats.map { it.id }
                            }
                        }.decodeList<SupabaseChatParticipant>()
                    }
                        .map(SupabaseChatParticipant::toChatParticipant)
                )
            }
            val chats = combine(
                flow = joinedChats,
                flow2 = settings,
                flow3 = otherUsers,
                flow4 = participants
            ) { chats, settings, otherUsers, participants ->
                chats.map { c ->
                    c.copy(
                        setting = if (c.type != ChatType.PERSONAL) settings
                            .firstOrNull { s -> s.first == c.id }
                            ?.second
                        else otherUsers
                            .firstOrNull { it.first == c.id }
                            ?.second
                            ?.let {
                                ChatSetting(
                                    name = it.username,
                                    iconPath = it.imagePath
                                )
                            },
                        participants = participants.filter { p -> p.chatId == c.id }
                    )
                }
            }
            val messages = chats.flatMapLatest { c ->
                performOperation(MESSAGES) {
                    selectAsFlow(
                        primaryKey = SupabaseMessage::id,
                        filter = FilterOperation(
                            column = "chat_id",
                            operator = FilterOperator.IN,
                            value = c.map { c -> c.id }.toInValues()
                        )
                    )
                        .map {
                            it.sortedByDescending { m -> m.sentAt }
                        }
                }
            }
            val senders = messages.flatMapLatest { m ->
                flowOf(
                    users {
                        select {
                            filter {
                                User::id isIn m.map { it.senderId }
                            }
                        }.decodeList<User>()
                    }
                )
            }
            val messageStatuses = messages.flatMapLatest { messages ->
                performOperation(MESSAGE_STATUSES) {
                    selectAsFlow(
                        primaryKeys = listOf(
                            MessageStatus::messageId,
                            MessageStatus::userId
                        ),
                        filter = FilterOperation(
                            column = "message_id",
                            operator = FilterOperator.IN,
                            value = messages.map { it.id }.toInValues()
                        )
                    )
                }
            }

            combine(
                flow = chats,
                flow2 = messages,
                flow3 = senders,
                flow4 = messageStatuses
            ) { chats, messages, senders, messageStatuses ->
                chats
                    .map { c ->
                        LatestChatMessage(
                            chat = c,
                            messageDetail = messages
                                .firstOrNull { m -> m.chatId == c.id }
                                ?.toMessage()?.let { m ->
                                    senders
                                        .firstOrNull { u -> u.id == m.senderId }
                                        ?.let { sender ->
                                            MessageDetail(
                                                sender = sender,
                                                message = m,
                                                messageStatuses = messageStatuses
                                                    .filter { ms -> ms.messageId == m.id }
                                            )
                                        }
                                }
                        )
                    }
                    .sortedWith(
                        compareByDescending<LatestChatMessage> {
                            it.messageDetail?.message?.sentAt
                        }
                            .thenByDescending { it.chat.createdAt }
                    )
                    .onEach {
                        Log.d(LOG_TAG, "latest chat: $it")
                    }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeActiveParticipants(chatId: String): Flow<List<ChatParticipant>> =
        userPresenceManager.activeUsers
            .flatMapLatest { userStatuses ->
                flowOf(
                    chatParticipants {
                        select {
                            filter {
                                SupabaseChatParticipant::chatId eq chatId
                                SupabaseChatParticipant::leftAt eq null
                            }
                        }.decodeList<SupabaseChatParticipant>()
                    }
                        .filter { p ->
                            userStatuses
                                .firstOrNull { us -> us.userId == p.userId } != null
                        }
                        .map(SupabaseChatParticipant::toChatParticipant)
                )
            }

    override suspend fun getJoinedChats(userId: String): ChatResult<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatParticipants(chatId: String): ChatResult<List<ChatParticipant>> =
        performSuspendingAuthenticatedAction { _ ->
            try {
                val participants = chatParticipants {
                    select {
                        filter {
                            SupabaseChatParticipant::chatId eq chatId
                        }
                    }.decodeList<SupabaseChatParticipant>()
                }
                    .map(SupabaseChatParticipant::toChatParticipant)

                Success(participants)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(ChatError.Unknown)
            }
        }

    override suspend fun getChatDetail(chatId: String): ChatResult<ChatDetail> {
        TODO("Not yet implemented")
    }

    override suspend fun joinChat(chatId: String): ChatResult<ChatParticipant> {
        TODO("Not yet implemented")
    }

    override suspend fun leaveChat(chatId: String): ChatResult<ChatParticipant> {
        TODO("Not yet implemented")
    }

    override suspend fun createChat(
        type: ChatType,
        chatSetting: CreateChatSetting?,
        participantIds: List<String>?
    ): ChatResult<Chat> = performSuspendingAuthenticatedAction { u ->
        if (type == ChatType.PERSONAL && participantIds?.size != 1)
            return@performSuspendingAuthenticatedAction Error(
                ChatError.ParticipantLimitViolation
            )
        if (type != ChatType.PERSONAL && chatSetting == null)
            return@performSuspendingAuthenticatedAction Error(
                ChatError.ChatSettingNotFound
            )

        try {
            chats {
                val newChat = insert(SupabaseCreateChat(type.toString())) {
                    select()
                }.decodeSingleOrNull<SupabaseChat>()

                newChat?.let { chat ->
                    var chatIconPath: String? = null
                    chatSetting?.icon?.let { fileUpload ->
                        with(Bucket.ICON) {
                            require(
                                type == ChatType.GROUP
                                    && allowedExtensions.contains(fileUpload.fileType)
                                    && fileUpload.content.isNotEmpty()
                            ) {
                                "Invalid file upload"
                            }
                            val path = createPath(
                                fileName = "${chat.id}.${fileUpload.fileType}",
                                rootFolder = type.toString()
                            )
                            try {
                                perform {
                                    upload(
                                        path = path.pathInBucket,
                                        data = fileUpload.content
                                    ) {
                                        this.userMetadata = buildJsonObject {
                                            put("user_id", u.id)
                                            put("email", u.email)
                                        }
                                    }
                                    chatIconPath = path.fullPath
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                return@chats Error(
                                    ChatError.FileUploadError
                                )
                            }
                        }
                    }
                    chatParticipants {
                        val admin = SupabaseCreateChatParticipant(
                            chatId = chat.id,
                            userId = u.id,
                            role = if (type != ChatType.PERSONAL) ParticipantRole.ADMIN.toString()
                            else ParticipantRole.MEMBER.toString()
                        )
                        val participants = participantIds?.map { uid ->
                            SupabaseCreateChatParticipant(
                                chatId = chat.id,
                                userId = uid,
                                role = ParticipantRole.MEMBER.toString()
                            )
                        }
                        insert(listOf(admin) + (participants ?: emptyList()))
                    }
                    val setting = if (type != ChatType.PERSONAL) chatSetting?.let { cs ->
                        val chatSetting = chatSettings {
                            insert(
                                value = cs.toSupabaseChatSetting(
                                    chatId = chat.id,
                                    iconPath = chatIconPath
                                )
                            ) {
                                select()
                            }.decodeSingleOrNull<SupabaseChatSetting>()
                        }
                        val permissionSettings = if (type == ChatType.GROUP) chatSetting?.let {
                            chatPermissionSettings {
                                insert(
                                    cs.permissionSettings?.toSupabaseChatPermissionSettings(chat.id)
                                        ?: ChatPermissionSettings().toSupabaseChatPermissionSettings(chat.id)
                                ) {
                                    select()
                                }.decodeSingleOrNull<SupabaseChatPermissionSettings>()
                            }
                        } else null
                        chatSetting?.toChatSetting(
                            permissionSettings?.toChatPermissionSettings()
                        )
                    } else null

                    Success(chat.toChat(setting))
                }
            } ?: Error(ChatError.Unknown)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(ChatError.Unknown)
        }
    }

    override suspend fun createEvent(
        chatId: String,
        title: String,
        description: String?,
        startsAt: Instant
    ): ChatResult<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(eventId: String): ChatResult<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun editEvent(
        eventId: String,
        title: String?,
        description: String?,
        startsAt: Instant
    ): ChatResult<Event> {
        TODO("Not yet implemented")
    }
}