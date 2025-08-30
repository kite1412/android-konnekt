package nrr.konnekt.feature.conversation.exception

internal data class AttachmentContentException(
    override val message: String? = "Invalid file content"
) : RuntimeException(message)