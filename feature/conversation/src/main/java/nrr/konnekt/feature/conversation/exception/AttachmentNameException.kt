package nrr.konnekt.feature.conversation.exception

internal data class AttachmentNameException(
    override val message: String? = "Invalid file name"
) : RuntimeException(message)
