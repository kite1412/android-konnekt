package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.ChatSettingEdit
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.dto.toChatSetting
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.model.UpdateChatParticipantStatus
import nrr.konnekt.core.domain.model.UserChatParticipation
import nrr.konnekt.core.domain.model.updateOrReset
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.ChatResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChat
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatInvitation
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipant
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipantStatus
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatSetting
import nrr.konnekt.core.network.supabase.dto.response.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUserMessageStatus
import nrr.konnekt.core.network.supabase.dto.response.rpc.GetChatParticipant
import nrr.konnekt.core.network.supabase.dto.response.rpc.model.SupabaseChatInvitationRpc
import nrr.konnekt.core.network.supabase.dto.response.rpc.model.SupabaseChatRpc
import nrr.konnekt.core.network.supabase.dto.response.rpc.model.toModel
import nrr.konnekt.core.network.supabase.dto.response.rpc.toChatParticipant
import nrr.konnekt.core.network.supabase.dto.response.toAttachment
import nrr.konnekt.core.network.supabase.dto.response.toChat
import nrr.konnekt.core.network.supabase.dto.response.toChatSetting
import nrr.konnekt.core.network.supabase.dto.response.toMessage
import nrr.konnekt.core.network.supabase.dto.response.toModel
import nrr.konnekt.core.network.supabase.dto.response.toUserChatParticipation
import nrr.konnekt.core.network.supabase.util.Bucket
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import nrr.konnekt.core.network.supabase.util.Tables.CHATS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_INVITATIONS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PARTICIPANTS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PARTICIPANT_STATUSES
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_PERMISSION_SETTINGS
import nrr.konnekt.core.network.supabase.util.Tables.CHAT_SETTINGS
import nrr.konnekt.core.network.supabase.util.Tables.MESSAGES
import nrr.konnekt.core.network.supabase.util.Tables.USERS
import nrr.konnekt.core.network.supabase.util.Tables.USER_MESSAGE_STATUSES
import nrr.konnekt.core.network.supabase.util.createPath
import nrr.konnekt.core.network.supabase.util.perform
import nrr.konnekt.core.network.supabase.util.toSupabaseEnum
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SupabaseChatRepository @Inject constructor(
    authentication: Authentication,
    private val userPresenceManager: SupabaseUserPresenceManager
) : ChatRepository, SupabaseService(authentication) {
    @OptIn(SupabaseExperimental::class)
    private val participatedIn by lazy {
        observeCurrentUserChatParticipations()
            .onEach { userParticipations ->
                Log.d(
                    LOG_TAG,
                    "joined chats: ${userParticipations
                        .map(UserChatParticipation::toString)}"
                )
            }
            .share()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val joinedChats by lazy {
        participatedIn.flatMapLatest { participations ->
            flow {
                emit(
                    chats {
                        select {
                            filter {
                                SupabaseChat::id isIn participations.map { c -> c.chatId }
                            }
                        }.decodeList<SupabaseChat>()
                    }
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
                        value = c.map { it.id }
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
                        value = s.map { it.chatId }
                    )
                )
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private val personalChatParticipants by lazy {
        performAuthenticatedAction { u ->
            joinedChats.flatMapLatest { c ->
                c.filter { it.type == ChatType.PERSONAL.toSupabaseEnum() }
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
                                    primaryKey = SupabaseUser::id,
                                    filter = FilterOperation(
                                        column = "id",
                                        operator = FilterOperator.IN,
                                        value = p.map { i -> i.userId }
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
                    }?.toModel()
                )
            }
        }
    }
    private val chats by lazy {
        combine(
            flow = joinedChats,
            flow2 = settings,
            flow3 = personalChatParticipants,
            flow4 = participatedIn
        ) { chats, settings, otherUsers, currentUserParticipations ->
            chats.map { c ->
                c
                    .toChat(
                        setting = if (c.type != ChatType.PERSONAL.toSupabaseEnum()) settings
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
                            }
                    )
                    .copy(
                        participants = currentUserParticipations
                            .filter { participation ->
                                participation.chatId == c.id
                            }
                            .map(UserChatParticipation::participation)
                    )
            }
        }
    }

    private var messageAttachments = emptyList<SupabaseAttachment>()

    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private val messages by lazy {
        chats.flatMapLatest { c ->
            performOperation(MESSAGES) {
                selectAsFlow(
                    primaryKey = SupabaseMessage::id,
                    filter = FilterOperation(
                        column = "chat_id",
                        operator = FilterOperator.IN,
                        value = c.map { c -> c.id }
                    )
                )
                    .map {
                        val messages = it
                            .sortedByDescending { m -> m.sentAt }
                            .distinctBy { m -> m.chatId }

                        messageAttachments = messages
                            .chunked(10).flatMap { l ->
                                attachments {
                                    select {
                                        filter {
                                            SupabaseAttachment::messageId isIn l.map { m -> m.id }
                                        }
                                    }
                                }
                                    .decodeList<SupabaseAttachment>()
                                    .distinctBy { l -> l.messageId }
                            }

                        messages
                    }
                    .share()
            }
        }
    }

    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    private val myMessageStatuses by lazy {
        messages.flatMapLatest { messages ->
            performOperation(USER_MESSAGE_STATUSES) {
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseUserMessageStatus::messageId,
                        SupabaseUserMessageStatus::userId
                    ),
                    filter = FilterOperation(
                        column = "message_id",
                        operator = FilterOperator.IN,
                        value = messages.map { it.id }
                    )
                ).share()
            }
        }
    }

    /*
        NOTE:
        - only observes current user message statuses
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeLatestChatMessages(): Flow<List<LatestChatMessage>> =
        performAuthenticatedAction { u ->
            val senders = messages.flatMapLatest { m ->
                flowOf(
                    users {
                        select {
                            filter {
                                SupabaseUser::id isIn m.map { it.senderId }
                            }
                        }
                            .decodeList<SupabaseUser>()
                            .map(SupabaseUser::toModel)
                    }
                )
            }

            combine(
                flow = chats,
                flow2 = messages,
                flow3 = senders,
                flow4 = myMessageStatuses
            ) { chats, messages, senders, myMessageStatuses ->
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
                                                messageStatuses = myMessageStatuses
                                                    .filter { ms ->
                                                        ms.messageId == m.id && ms.userId == u.id
                                                    }
                                                    .map {
                                                        it.toModel(user = u)
                                                    },
                                                attachments = messageAttachments.filter { a ->
                                                    a.messageId == m.id
                                                }.map(SupabaseAttachment::toAttachment)
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

    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    override fun observeChat(chatId: String): Flow<Chat> {
        val chat = combine(
            flow = performOperation(CHATS) {
                selectSingleValueAsFlow(
                    primaryKey = SupabaseChat::id
                ) {
                    filter(
                        FilterOperation(
                            column = "id",
                            operator = FilterOperator.EQ,
                            value = chatId
                        )
                    )
                }
            }
                .distinctUntilChanged { old, new ->
                    old.deletedAt == new.deletedAt
                }
                .share(),
            flow2 = observeChatParticipants(chatId).share()
        ) { chat, chatParticipants ->
            chat.toChat()
                .copy(
                    participants = chatParticipants
                )
        }

        val chatSetting = chat.flatMapLatest { chat ->
            if (chat.type != ChatType.PERSONAL) performOperation(CHAT_SETTINGS) {
                selectSingleValueAsFlow(
                    primaryKey = SupabaseChatSetting.PrimaryKey
                ) {
                    filter(
                        operation = FilterOperation(
                            column = "chat_id",
                            operator = FilterOperator.EQ,
                            value = chat.id
                        )
                    )
                }
                    .map(SupabaseChatSetting::toChatSetting)
            } else performOperation(CHAT_PARTICIPANTS) { user ->
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseChatParticipant::userId,
                        SupabaseChatParticipant::chatId
                    ),
                    filter = FilterOperation(
                        column = "chat_id",
                        operator = FilterOperator.EQ,
                        value = chatId
                    )
                )
                    .map { participants ->
                        participants.firstOrNull { participants ->
                            participants.userId != user.id
                        }
                    }
                    .flatMapLatest { participant ->
                        participant?.let { participant ->
                            performOperation(USERS) {
                                selectSingleValueAsFlow(
                                    primaryKey = SupabaseUser::id
                                ) {
                                    filter(
                                        column = "id",
                                        operator = FilterOperator.EQ,
                                        value = participant.userId
                                    )
                                }
                                    .map { user ->
                                        ChatSetting(
                                            name = user.username,
                                            description = user.bio,
                                            iconPath = user.imagePath
                                        )
                                    }
                            }
                        } ?: flowOf<ChatSetting?>(null)
                    }
            }
        }.share()

        val permissionSettings = chat.flatMapLatest { chat ->
            if (chat.type != ChatType.PERSONAL) performOperation(CHAT_PERMISSION_SETTINGS) {
                selectSingleValueAsFlow(
                    primaryKey = SupabaseChatPermissionSettings.PrimaryKey
                ) {
                    filter(
                        column = "chat_id",
                        operator = FilterOperator.EQ,
                        value = chat.id
                    )
                }
                    .map(SupabaseChatPermissionSettings::toModel)
                    .share()
            } else flowOf(null)
        }

        return combine(
            flow = chat,
            flow2 = chatSetting,
            flow3 = permissionSettings
        ) { chat, setting, permissionSettings ->
            chat.copy(
                setting = setting?.copy(
                    permissionSettings = permissionSettings
                )
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeActiveParticipants(chatId: String): Flow<List<ChatParticipant>> =
        userPresenceManager.activeUsers
            .flatMapLatest { userStatuses ->
                if (userStatuses.isNotEmpty()) flowOf(
                    rpc.getChatParticipants(chatId)
                        ?.filter { p ->
                            userStatuses.firstOrNull { us -> us.userId == p.user.id } != null
                        }
                        ?.map(GetChatParticipant::toChatParticipant)
                        ?: emptyList()
                ) else emptyFlow()
            }

    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    override fun observeChatParticipants(chatId: String): Flow<List<ChatParticipant>> {
        val participants = performOperation(CHAT_PARTICIPANTS) {
            selectAsFlow(
                primaryKeys = listOf(
                    SupabaseChatParticipant::userId,
                    SupabaseChatParticipant::chatId
                ),
                filter = FilterOperation(
                    column = "chat_id",
                    operator = FilterOperator.EQ,
                    value = chatId
                )
            )
        }
        val participantStatuses = performOperation(CHAT_PARTICIPANT_STATUSES) {
            selectAsFlow(
                primaryKeys = listOf(
                    SupabaseChatParticipantStatus::chatId,
                    SupabaseChatParticipantStatus::userId
                ),
                filter = FilterOperation(
                    column = "chat_id",
                    operator = FilterOperator.EQ,
                    value = chatId
                )
            )
        }
        val users = participants.flatMapLatest { participants ->
            performOperation(USERS) {
                selectAsFlow(
                    primaryKey = SupabaseUser::id,
                    filter = FilterOperation(
                        column = "id",
                        operator = FilterOperator.IN,
                        value = participants.map(SupabaseChatParticipant::userId)
                    )
                )
                    .map { list ->
                        list.map(SupabaseUser::toModel)
                    }
            }
        }

        return combine(
            flow = participants,
            flow2 = participantStatuses,
            flow3 = users
        ) { participants, statuses, users ->
            participants.map { participant ->
                participant.toModel(
                    user = users.first { u -> u.id == participant.userId },
                    status = statuses
                        .first { status ->
                            status.userId == participant.userId
                        }
                        .toModel()
                )
            }
        }
    }

    @OptIn(SupabaseExperimental::class)
    override fun observeCurrentUserChatParticipation(chatId: String): Flow<UserChatParticipation?> =
        performAuthenticatedAction { user ->
            val participation = performOperation(CHAT_PARTICIPANTS) {
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseChatParticipant::userId,
                        SupabaseChatParticipant::chatId
                    ),
                    filter = FilterOperation(
                        column = "user_id",
                        operator = FilterOperator.EQ,
                        value = user.id
                    )
                )
                    .map { participants ->
                        participants.firstOrNull { status ->
                            status.chatId == chatId
                        }
                    }
            }
            val status = performOperation(CHAT_PARTICIPANT_STATUSES) {
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseChatParticipantStatus::userId,
                        SupabaseChatParticipantStatus::chatId
                    ),
                    filter = FilterOperation(
                        column = "user_id",
                        operator = FilterOperator.EQ,
                        value = user.id
                    )
                )
                    .map { statuses ->
                        statuses.firstOrNull { status ->
                            status.chatId == chatId
                        }
                    }
            }

            combine(
                flow = participation,
                flow2 = status
            ) { participation, status ->
                status?.let { status ->
                    participation?.toUserChatParticipation(
                        user = user,
                        status = status.toModel()
                    )
                }
            }
        }

    @OptIn(SupabaseExperimental::class)
    override fun observeCurrentUserChatParticipations(): Flow<List<UserChatParticipation>> =
        performOperation(CHAT_PARTICIPANTS) { user ->
            val participants = selectAsFlow(
                primaryKeys = listOf(
                    SupabaseChatParticipant::userId,
                    SupabaseChatParticipant::chatId
                ),
                filter = FilterOperation(
                    column = "user_id",
                    operator = FilterOperator.EQ,
                    value = user.id
                )
            )
            val statuses = performOperation(CHAT_PARTICIPANT_STATUSES) {
                selectAsFlow(
                    primaryKeys = listOf(
                        SupabaseChatParticipantStatus::userId,
                        SupabaseChatParticipantStatus::chatId
                    ),
                    filter = FilterOperation(
                        column = "user_id",
                        operator = FilterOperator.EQ,
                        value = user.id
                    )
                )
            }

            combine(
                flow = participants,
                flow2 = statuses
            ) { participants, statuses ->
                participants.mapNotNull { participant ->
                    statuses
                        .firstOrNull { status ->
                            status.chatId == participant.chatId
                        }
                        ?.let { status ->
                            participant.toUserChatParticipation(
                                user = user,
                                status = status.toModel()
                            )
                        }
                }
            }
        }

    @OptIn(SupabaseExperimental::class)
    override fun observeCurrentUserChatInvitations(): Flow<List<ChatInvitation>> =
        performOperation(CHAT_INVITATIONS) { user ->
            selectAsFlow(
                primaryKey = SupabaseChatInvitation.PrimaryKey,
                filter = FilterOperation(
                    column = "receiver_id",
                    operator = FilterOperator.EQ,
                    value = user.id
                )
            )
                .map {
                    val res = getUserChatInvitations(user.id)

                    if (res is Result.Success) res.data
                    else emptyList()
                }
        }

    @OptIn(SupabaseExperimental::class)
    override fun observeChatInvitations(chatId: String): Flow<List<ChatInvitation>> =
        performOperation(CHAT_INVITATIONS) {
            selectAsFlow(
                primaryKey = SupabaseChatInvitation.PrimaryKey,
                filter = FilterOperation(
                    column = "chat_id",
                    operator = FilterOperator.EQ,
                    value = chatId
                )
            )
                .map { _ ->
                    val res = getChatInvitations(chatId)

                    if (res is Result.Success) res.data
                    else emptyList()
                }
        }

    override suspend fun updateCurrentUserChatParticipantStatus(
        update: UpdateChatParticipantStatus
    ): ChatResult<ChatParticipantStatus> = with(update) {
        rpc.updateChatParticipantStatus(
            chatId = chatId,
            updateLeftAt = updateLeftAt.updateOrReset(),
            updateClearedAt = updateClearedAt.updateOrReset(),
            updateArchivedAt = updateArchivedAt.updateOrReset(),
            updateLastReadAt = updateLastReadAt.updateOrReset()
        )
            ?.let(Result<ChatParticipantStatus, Nothing>::Success)
            ?: Error(ChatError.Unknown)
    }

    override suspend fun updateChatSetting(
        chatId: String,
        chatSetting: ChatSettingEdit
    ): ChatResult<ChatSetting> =
        try {
            val chat = chats {
                select {
                    filter {
                        SupabaseChat::id eq chatId
                    }
                    limit(1)
                }.decodeSingleOrNull<SupabaseChat>()
            }
                ?.let {
                    val chat = it.toChat()

                    if (chat.type == ChatType.PERSONAL) {
                        return Error(ChatError.ParticipantRoleViolation)
                    }

                    val setting = chatSettings {
                        select {
                            filter {
                                SupabaseChatSetting::chatId eq chatId
                            }
                            limit(1)
                        }
                    }
                        .decodeSingleOrNull<SupabaseChatSetting>()

                    setting?.let { setting ->
                        chat.copy(
                            setting = setting.toChatSetting(
                                permissionSettings = chatSetting.permissionSettings
                            )
                        )
                    }
                } ?: return Error(ChatError.ChatNotFound)

            var newIconPath: String? = null
            chatSetting.icon?.let { newIcon ->
                val res = updateChatIcon(
                    chat = chat,
                    newIcon = newIcon
                )

                if (res is Result.Success) newIconPath = res.data
                else return@let res
            }

            val res = rpc.updateChatSetting(
                chatId = chatId,
                chatSetting = chatSetting.toChatSetting(
                    iconPath = newIconPath ?: chat.setting?.iconPath
                )
            )?.toModel()

            res
                ?.let(Result<ChatSetting, Nothing>::Success)
                ?: Error(ChatError.Unknown)
        } catch (_: Exception) {
            Error(ChatError.Unknown)
        }

    override suspend fun getChatById(chatId: String): ChatResult<Chat> =
        try {
            rpc.getChatById(chatId)
                ?.toModel()
                ?.let(Result<Chat, Nothing>::Success)
                ?: Error(ChatError.Unknown)
        } catch (_: Exception) {
            Error(ChatError.ChatNotFound)
        }

    override suspend fun getJoinedChats(userId: String, type: ChatType?): ChatResult<List<Chat>> =
        rpc.getJoinedChats(userId, type)
            ?.let { chats ->
                Success(chats.map(SupabaseChatRpc::toModel))
            }
            ?: Error(ChatError.Unknown)

    override suspend fun getChatParticipants(chatId: String): ChatResult<List<ChatParticipant>> =
        try {
            val participants = rpc.getChatParticipants(chatId)
                ?.map(GetChatParticipant::toChatParticipant)
                ?: emptyList()

            Success(participants)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(ChatError.Unknown)
        }

    override suspend fun joinChat(invitationId: String): ChatResult<ChatParticipant> =
        performSuspendingAuthenticatedAction {
            val res = rpc.joinChat(invitationId)

            res
                ?.toModel()
                ?.let(Result<ChatParticipant, Nothing>::Success)
                ?: Error(ChatError.Unknown)
        }

    override suspend fun leaveChat(chatId: String): ChatResult<ChatParticipant> =
        performSuspendingAuthenticatedAction { user ->
            val res = rpc.updateChatParticipantStatus(
                chatId = chatId,
                updateLeftAt = true
            )

            res?.let { status ->
                val participant = getCurrentUserChatParticipation(chatId)

                participant?.let {
                    Success(
                        data = participant.toModel(
                            user = user,
                            status = status
                        )
                    )
                }
            } ?: Error(ChatError.Unknown)
        }

    override suspend fun createChat(
        type: ChatType,
        chatSetting: ChatSettingEdit?,
        participantIds: List<String>?
    ): ChatResult<Chat> = performSuspendingAuthenticatedAction {
        if (type == ChatType.PERSONAL && participantIds?.size != 1)
            return@performSuspendingAuthenticatedAction Error(
                ChatError.ParticipantLimitViolation
            )
        if (type != ChatType.PERSONAL && chatSetting == null)
            return@performSuspendingAuthenticatedAction Error(
                ChatError.ChatSettingNotFound
            )

        val res = rpc.createChat(
            type = type.toSupabaseEnum(),
            participantIds = participantIds ?: emptyList(),
            name = chatSetting?.name,
            description = chatSetting?.description,
            iconPath = null,
            permissionSettings = chatSetting?.permissionSettings
        )
        res?.let { chat ->
            val chatIconPath = chatSetting?.icon?.let { newIcon ->
                updateChatIcon(
                    chat = chat.toModel(),
                    newIcon = newIcon
                )
            }

            if (chatIconPath == null || chatIconPath is Result.Error)
                return@let chatIconPath


            Success(
                data = chat.toModel().run {
                    copy(
                        setting = setting?.copy(
                            iconPath = (chatIconPath as Result.Success).data
                        )
                    )
                }
            )
        } ?: Error(ChatError.Unknown)
    }

    override suspend fun deleteChat(chatId: String): ChatResult<Chat> =
        rpc.deleteChat(chatId)
            ?.toModel()
            ?.let(Result<Chat, Nothing>::Success)
            ?: Error(ChatError.ParticipantRoleViolation)

    override suspend fun inviteToChat(
        chatId: String,
        receiverIds: List<String>
    ): ChatResult<List<ChatInvitation>> =
        rpc.inviteToChat(
            chatId = chatId,
            receiverIds = receiverIds
        )
        ?.let { invitations ->
            Success(invitations.map(SupabaseChatInvitationRpc::toModel))
        }
        ?: Error(ChatError.ParticipantRoleViolation)

    override suspend fun cancelChatInvitations(
        invitationIds: List<String>
    ): ChatResult<Boolean> =
        rpc.cancelChatInvitations(invitationIds)
            ?.let(Result<Boolean, Nothing>::Success)
            ?: Error(ChatError.Unknown)

    private suspend fun getCurrentUserChatParticipation(chatId: String) =
        performSuspendingAuthenticatedAction { user ->
            chatParticipants {
                select {
                    filter {
                        SupabaseChatParticipant::userId eq user.id
                        SupabaseChatParticipant::chatId eq chatId
                    }
                    limit(1)
                }
            }
                .decodeSingleOrNull<SupabaseChatParticipant>()
        }

    private fun <T> Flow<T>.share() = shareIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5_000),
        replay = 1
    )

    private suspend fun updateChatIcon(chat: Chat, newIcon: FileUpload): ChatResult<String> =
        performSuspendingAuthenticatedAction { user ->
            with(Bucket.ICON) {
                if(
                    chat.type != ChatType.PERSONAL &&
                    newIcon.content.isNotEmpty() &&
                    allowedExtensions.contains(newIcon.fileExtension)
                ) Error(ChatError.FileUploadError)

                val chatType = chat.type.toSupabaseEnum()
                val path = createPath(
                    fileName = "${chat.id}/${now()}.${newIcon.fileExtension}",
                    rootFolder = chatType
                )
                try {
                    perform {
                        delete("$chatType/${chat.id}/")
                        upload(
                            path = path.pathInBucket,
                            data = newIcon.content
                        ) {
                            this.userMetadata = buildJsonObject {
                                put("user_id", user.id)
                                put("email", user.email)
                            }
                        }

                        chatSettings {
                            update(
                                update = {
                                    SupabaseChatSetting::iconPath setTo path.fullPath
                                }
                            ) {
                                filter {
                                    SupabaseChatSetting::chatId eq chat.id
                                }
                            }
                        }

                        Success(path.fullPath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Error(ChatError.FileUploadError)
                }
            }
        }

    private suspend fun getUserChatInvitations(userId: String): ChatResult<List<ChatInvitation>> =
        rpc.getUserChatInvitations(userId)
            ?.let { invitations ->
                Success(invitations.map(SupabaseChatInvitationRpc::toModel))
            }
            ?: Error(ChatError.Unknown)

    private suspend fun getChatInvitations(chatId: String): ChatResult<List<ChatInvitation>> =
        rpc.getChatInvitations(chatId)
            ?.let { invitations ->
                Success(invitations.map(SupabaseChatInvitationRpc::toModel))
            }
            ?: Error(ChatError.Unknown)
}