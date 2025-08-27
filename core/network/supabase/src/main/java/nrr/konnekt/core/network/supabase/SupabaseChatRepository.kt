package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.model.ChatDetail
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.ChatResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
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
    @OptIn(SupabaseExperimental::class)
    private val participatedIn by lazy {
        performAuthenticatedAction { u ->
            performOperation(CHAT_PARTICIPANTS) {
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseChatParticipant::userId,
                        SupabaseChatParticipant::chatId
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
                    }
                    .share()
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val joinedChats by lazy {
        participatedIn.flatMapLatest { jc ->
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
    }
    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private val chatSettings by lazy {
        joinedChats.flatMapLatest { c ->
            performOperation(CHAT_SETTINGS) {
                selectAsFlow(
                    primaryKey = SupabaseChatSetting::chatId,
                    filter = FilterOperation(
                        column = "chat_id",
                        operator = FilterOperator.IN,
                        value = c.map { it.id }.toInValues()
                    )
                ).share()
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private val chatPermissionSettings by lazy {
        chatSettings.flatMapLatest { s ->
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
    }
    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private val personalChatParticipants by lazy {
        performAuthenticatedAction { u ->
            joinedChats.flatMapLatest { c ->
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
                                            p.first { it.userId == u.id }.chatId to u
                                        }
                                    }
                                    .share()
                            } else flowOf(emptyList())
                        }
                    }
            }
        }
    }
    private val settings by lazy {
        combine(
            flow = chatSettings,
            flow2 = chatPermissionSettings
        ) { settings, permissionSettings ->
            settings.map { s ->
                s.chatId to s.toChatSetting(
                    permissionSettings = permissionSettings.firstOrNull { ps ->
                        ps.chatId == s.chatId
                    }?.toChatPermissionSettings()
                )
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val participants by lazy {
        joinedChats.flatMapLatest { chats ->
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
    }
    private val chats by lazy {
        combine(
            flow = joinedChats,
            flow2 = settings,
            flow3 = personalChatParticipants,
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
    }
    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private val messages by lazy {
        chats.flatMapLatest { c ->
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
                    .share()
            }
        }
    }
    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    private val messageStatuses by lazy {
        messages.flatMapLatest { messages ->
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
                ).share()
            }
        }
    }

    private fun List<String>.toInValues(): String =
        joinToString(
            separator = ",",
            prefix = "(",
            postfix = ")"
        )

    private fun <T> Flow<T>.share() = shareIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5_000),
        replay = 1
    )

    /*
        TODO:
         - resolve messages' attachments
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeLatestChatMessages(): Flow<List<LatestChatMessage>> =
        performAuthenticatedAction { u ->
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
                            message = messages
                                .firstOrNull { m -> m.chatId == c.id }
                                ?.let { m ->
                                    senders
                                        .firstOrNull { u -> u.id == m.senderId }
                                        ?.let { s ->
                                            m.toMessage(s).copy(
                                                messageStatuses = messageStatuses
                                                    .filter { ms -> ms.messageId == m.id }
                                            )
                                        }
                                }
                        )
                    }
                    .sortedWith(
                        compareByDescending<LatestChatMessage> {
                            it.message?.sentAt
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

    override suspend fun getChatById(chatId: String): ChatResult<Chat> =
        try {
            chats {
                select {
                    filter {
                        SupabaseChat::id eq chatId
                    }
                }.decodeSingleOrNull<SupabaseChat>()
            }
                ?.toChat()
                ?.run {
                    copy(
                        setting = if (type != ChatType.PERSONAL) chatSettings {
                            select {
                                filter {
                                    SupabaseChatSetting::chatId eq id
                                }
                            }.decodeSingleOrNull<SupabaseChatSetting>()
                        }?.toChatSetting(
                            permissionSettings = chatPermissionSettings {
                                select {
                                    filter {
                                        SupabaseChatPermissionSettings::chatId eq id
                                    }
                                }.decodeSingleOrNull<SupabaseChatPermissionSettings>()
                            }?.toChatPermissionSettings()
                        ) else getPersonalChatSetting(chatId)
                            ?: return Error(ChatError.ChatNotFound),
                        participants = (getChatParticipants(chatId)
                            .takeIf {
                                it is Result.Success
                            } as? Result.Success<List<ChatParticipant>>)
                            ?.data ?: emptyList()
                    )
                }
                ?.let {
                    Success(it)
                } ?: Error(ChatError.ChatNotFound)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(ChatError.Unknown)
        }

   private suspend fun getPersonalChatSetting(chatId: String) =
        performSuspendingAuthenticatedAction { u ->
            chatParticipants {
                select {
                    filter {
                        SupabaseChatParticipant::chatId eq chatId
                        SupabaseChatParticipant::userId neq u.id
                    }
                }
                    .decodeSingleOrNull<SupabaseChatParticipant>()
                    ?.let {
                        users {
                            select {
                                filter {
                                    User::id eq it.userId
                                }
                            }
                                .decodeSingleOrNull<User>()
                                ?.let { user ->
                                    ChatSetting(
                                        name = user.username,
                                        iconPath = user.imagePath
                                    )
                                }
                        }
                    }
            }
        }

    override suspend fun getJoinedChats(userId: String): ChatResult<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatParticipants(chatId: String): ChatResult<List<ChatParticipant>> =
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
                                    && allowedExtensions.contains(fileUpload.fileExtension)
                                    && fileUpload.content.isNotEmpty()
                        ) {
                            "Invalid file upload"
                        }
                        val path = createPath(
                            fileName = "${chat.id}.${fileUpload.fileExtension}",
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