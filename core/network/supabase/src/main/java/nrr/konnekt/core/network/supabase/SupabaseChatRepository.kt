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
import nrr.konnekt.core.domain.dto.CreateChatSetting
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
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChat
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipant
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipantStatus
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatSetting
import nrr.konnekt.core.network.supabase.dto.response.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUserMessageStatus
import nrr.konnekt.core.network.supabase.dto.response.rpc.GetChatParticipant
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
            .map { userParticipations ->
                userParticipations.filter { userParticipation ->
                    userParticipation.participation.status.leftAt == null
                }
            }
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
                            }?.toModel()
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

    override suspend fun getJoinedChats(userId: String): ChatResult<List<Chat>> =
        rpc.getJoinedChats(userId)
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

    override suspend fun joinChat(chatId: String): ChatResult<ChatParticipant> =
        performSuspendingAuthenticatedAction {
            val res = rpc.joinChat(chatId)

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

        val res = rpc.createChat(
            type = type.toSupabaseEnum(),
            participantIds = participantIds ?: emptyList(),
            name = chatSetting?.name,
            description = chatSetting?.description,
            iconPath = null,
            permissionSettings = chatSetting?.permissionSettings
        )
        var chatIconPath: String? = null

        res?.let { chat ->
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
                        rootFolder = type.toSupabaseEnum()
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
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@let Error(
                            ChatError.FileUploadError
                        )
                    }
                }
            }

            Success(
                data = chat.toModel().run {
                    copy(
                        setting = setting?.copy(
                            iconPath = chatIconPath
                        )
                    )
                }
            )
        } ?: Error(ChatError.Unknown)
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
                                    SupabaseUser::id eq it.userId
                                }
                            }
                                .decodeSingleOrNull<SupabaseUser>()
                                ?.toModel()
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

    private suspend fun getCurrentUserChatParticipantStatuses() =
        performSuspendingAuthenticatedAction { user ->
            chatParticipantStatuses {
                select {
                    filter {
                        SupabaseChatParticipantStatus::userId eq user.id
                    }
                }
            }
                .decodeList<SupabaseChatParticipantStatus>()
        }

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
}