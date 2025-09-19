package nrr.konnekt.feature.conversation.exception

internal data class AttachmentSizeException(
    override val message: String?
): RuntimeException(message)