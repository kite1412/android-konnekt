package nrr.konnekt.core.domain.exception

data class FileUploadConstraintViolationException(
    override val message: String?,
    val reason: FileUploadConstraintViolationExceptionReason
) : RuntimeException()

enum class FileUploadConstraintViolationExceptionReason {
    SIZE_EXCEEDED,
    SIZE_INVALID
}