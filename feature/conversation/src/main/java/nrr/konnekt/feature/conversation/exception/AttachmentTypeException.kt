package nrr.konnekt.feature.conversation.exception

internal data class AttachmentTypeException(
    override val message: String? = "Invalid file type"
) : RuntimeException(message)
