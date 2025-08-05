package nrr.konnekt.core.network.supabase

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.model.ChatDetail
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.domain.repository.ChatRepository
import nrr.konnekt.core.domain.repository.ChatRepository.ChatError
import nrr.konnekt.core.domain.repository.ChatResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Event
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.network.supabase.dto.SupabaseChat
import nrr.konnekt.core.network.supabase.dto.SupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.SupabaseChatSetting
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateChat
import nrr.konnekt.core.network.supabase.dto.request.SupabaseCreateChatParticipant
import nrr.konnekt.core.network.supabase.dto.toChat
import nrr.konnekt.core.network.supabase.dto.toChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.toChatSetting
import nrr.konnekt.core.network.supabase.dto.toSupabaseChatPermissionSettings
import nrr.konnekt.core.network.supabase.dto.toSupabaseChatSetting
import javax.inject.Inject
import kotlin.time.Instant

internal class SupabaseChatRepository @Inject constructor(
    authentication: Authentication
) : ChatRepository, SupabaseRepository(authentication) {
    override fun observeLatestChatMessages(): Flow<List<LatestChatMessage>> {
        TODO("Not yet implemented")
    }

    override fun observeActiveParticipants(chatId: String): Flow<List<ChatParticipant>> {
        TODO("Not yet implemented")
    }

    override suspend fun getJoinedChats(userId: String): ChatResult<List<Chat>> {
        TODO("Not yet implemented")
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

    // TODO implement chat's icon upload
    override suspend fun createChat(
        type: ChatType,
        chatSetting: CreateChatSetting?,
        participantIds: List<String>?
    ): ChatResult<Chat> = performAuthenticatedAction { u ->
        if (type == ChatType.PERSONAL && participantIds?.size != 1)
            return@performAuthenticatedAction Error(
                ChatError.ParticipantLimitViolation
            )
        if (type != ChatType.PERSONAL && chatSetting == null)
            return@performAuthenticatedAction Error(
                ChatError.ChatSettingNotFound
            )

        try {
            chats {
                val newChat = insert(SupabaseCreateChat(type.toString())) {
                    select()
                }.decodeSingleOrNull<SupabaseChat>()

                newChat?.let { chat ->
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
                            insert(cs.toSupabaseChatSetting(chat.id)) {
                                select()
                            }.decodeSingleOrNull<SupabaseChatSetting>()
                        }
                        val permissionSettings = chatSetting?.let {
                            chatPermissionSettings {
                                insert(
                                    cs.permissionSettings.toSupabaseChatPermissionSettings(chat.id)
                                ) {
                                    select()
                                }.decodeSingleOrNull<SupabaseChatPermissionSettings>()
                            }
                        }
                        permissionSettings?.let {
                            chatSetting.toChatSetting(
                                permissionSettings = it.toChatPermissionSettings(),
                                iconPath = null
                            )
                        }
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