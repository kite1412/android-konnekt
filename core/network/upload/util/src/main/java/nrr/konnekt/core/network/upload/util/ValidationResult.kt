package nrr.konnekt.core.network.upload.util

sealed interface ValidationResult {
    data object Valid: ValidationResult
    data class Invalid(val reason: ViolationReason): ValidationResult
}