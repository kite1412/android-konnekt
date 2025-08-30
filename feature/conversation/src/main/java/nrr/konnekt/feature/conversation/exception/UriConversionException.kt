package nrr.konnekt.feature.conversation.exception

internal data class UriConversionException(
    override val message: String?
) : RuntimeException(message)
