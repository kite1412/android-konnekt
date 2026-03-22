package nrr.konnekt.core.domain

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.common.result.Error
import nrr.konnekt.core.common.result.Result
import nrr.konnekt.core.domain.Authentication.AuthError
import nrr.konnekt.core.domain.util.AuthStatus
import nrr.konnekt.core.model.User

typealias AuthResult<T> = Result<T, AuthError>

/**
 * Contract for authentication service and act as
 * a single source of truth for logged-in user data used in DataSources impl
 * to perform user-specific actions.
 */
interface Authentication {
    /**
     * A flow of the authentication status.
     */
    val authStatus: Flow<AuthStatus>

    /**
     * A flow of the logged-in user data.
     */
    val loggedInUser: Flow<User?>

    /**
     * Get the logged-in user.
     *
     * @return The logged-in user.
     */
    fun getLoggedInUserOrNull(): User?

    /**
     * Login with username and password.
     *
     * @param email The email to log in with.
     * @param password The password to log in with.
     * @return The logged-in user.
     */
    suspend fun login(email: String, password: String): AuthResult<User>

    /**
     * Register with email and password.
     *
     * @param email The email to register with.
     * @param username The username to register with.
     * @param password The password to register with.
     * @return The registered user.
     */
    suspend fun register(email: String, username: String, password: String): AuthResult<User>

    /**
     * Logout the current user.
     *
     * @return Whether the logout was successful.
     */
    suspend fun logout(): AuthResult<Boolean>

    /**
     * Store the FCM token for the current user.
     *
     * @param token The FCM token to store.
     */
    suspend fun storeFcmToken(token: String): AuthResult<Boolean>

    sealed interface AuthError : Error {
        object InvalidCredentials : AuthError
        object UserAlreadyExists : AuthError
        object EmailNotConfirmed : AuthError
        object UnauthenticatedAction: AuthError
        object Unknown : AuthError
    }
}