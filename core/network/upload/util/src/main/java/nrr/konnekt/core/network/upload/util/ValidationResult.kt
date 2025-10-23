package nrr.konnekt.core.network.upload.util

import nrr.konnekt.core.network.upload.util.exception.FileUploadConstraintViolationException

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(
        val exception: FileUploadConstraintViolationException
    ) : ValidationResult
}