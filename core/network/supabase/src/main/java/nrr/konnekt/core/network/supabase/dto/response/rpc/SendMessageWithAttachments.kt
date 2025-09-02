package nrr.konnekt.core.network.supabase.dto.response.rpc

import kotlinx.serialization.Serializable
import nrr.konnekt.core.network.supabase.dto.SupabaseMessage
import nrr.konnekt.core.network.supabase.dto.response.SupabaseAttachment

@Serializable
internal data class SendMessageWithAttachments(
    val message: SupabaseMessage,
    val attachments: List<SupabaseAttachment>
)