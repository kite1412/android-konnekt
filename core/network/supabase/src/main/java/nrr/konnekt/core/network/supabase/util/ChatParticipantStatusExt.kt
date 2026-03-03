package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.network.supabase.dto.response.SupabaseChatParticipantStatus

internal fun ChatParticipantStatus.toSupabaseModel(userId: String, chatId: String) =
    SupabaseChatParticipantStatus(
        userId = userId,
        chatId = chatId,
        joinedAt = joinedAt,
        lastReadAt = lastReadAt,
        leftAt = leftAt,
        clearedAt = clearedAt,
        archivedAt = archivedAt
    )