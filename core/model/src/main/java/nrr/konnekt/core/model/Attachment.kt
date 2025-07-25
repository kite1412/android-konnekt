package nrr.konnekt.core.model

data class Attachment(
    val id: String,
    val type: AttachmentType,
    val path: String,
    val name: String?,
    val size: Long?,
    val metadata: AttachmentMetadata?
)