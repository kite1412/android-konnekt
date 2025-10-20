package nrr.konnekt.core.ui

data class UriException(
    override val message: String?,
    val reason: UriExceptionReason
) : RuntimeException()

enum class UriExceptionReason {
    INVALID_FILE_NAME,
    INVALID_CONTENT
}