package nrr.konnekt.core.domain.exception

/**
 * Exception thrown when the user is not authenticated.
 */
data class UnauthenticatedException(
    override val message: String = "Missing or invalid credentials."
): RuntimeException(message)
