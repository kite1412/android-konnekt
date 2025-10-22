package nrr.konnekt.core.network.upload.util

enum class ViolationReason {
    FILE_SIZE_TOO_LARGE,
    UNSUPPORTED_MIME_TYPE,
    FILE_NOT_FOUND,
    FILE_SIZE_INVALID,
    INVALID_MIME_TYPE
}