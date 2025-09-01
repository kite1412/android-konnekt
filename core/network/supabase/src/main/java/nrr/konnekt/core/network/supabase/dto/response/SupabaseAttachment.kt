package nrr.konnekt.core.network.supabase.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentMetadata
import nrr.konnekt.core.model.AttachmentType

@Serializable
internal data class SupabaseAttachment(
    val id: String,
    @SerialName("message_id")
    val messageId: String,
    @SerialName("chat_id")
    val chatId: String,
    val type: String,
    val path: String,
    val name: String,
    val size: Long? = null
)

internal fun SupabaseAttachment.toAttachment(
    metadata: AttachmentMetadata? = null
) = Attachment(
    id = id,
    type = rawToAttachmentType(type),
    path = path,
    name = name,
    size = size,
    metadata = metadata
)

private fun rawToAttachmentType(type: String) =
    when (type) {
        "image" -> AttachmentType.IMAGE
        "video" -> AttachmentType.VIDEO
        "audio" -> AttachmentType.AUDIO
        "document" -> AttachmentType.DOCUMENT
        else -> throw IllegalArgumentException("Invalid file type: $type")
    }