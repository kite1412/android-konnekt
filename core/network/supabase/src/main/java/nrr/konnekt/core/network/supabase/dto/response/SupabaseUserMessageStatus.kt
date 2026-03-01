package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserMessageStatus

@Serializable
internal data class SupabaseUserMessageStatus(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("is_deleted")
    val isDeleted: Boolean
)

internal fun SupabaseUserMessageStatus.toModel(user: User) = UserMessageStatus(
    user = user,
    isDeleted = isDeleted
)