package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.domain.model.UserChatParticipation
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.model.User

@Serializable
internal data class SupabaseChatParticipant(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: String,
    val role: String
)

internal fun SupabaseChatParticipant.toModel(user: User, status: ChatParticipantStatus) =
    ChatParticipant(
        user = user,
        role = ParticipantRole.valueOf(role.uppercase()),
        status = status
    )

internal fun SupabaseChatParticipant.toUserChatParticipation(
    user: User,
    status: ChatParticipantStatus
) = UserChatParticipation(
    chatId = chatId,
    participation = toModel(user, status)
)