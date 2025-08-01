package nrr.konnekt.core.network.api

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.model.User

/**
 * Contract for authentication service and act as
 * a single source of truth for logged in user data used in DataSources impl
 * to perform user-specific actions.
 */
interface Authentication {
    /**
     * A flow of the logged in user data.
     */
    val loggedInUser: Flow<User?>

    /**
     * Login with username and password.
     *
     * @param email The email to login with.
     * @param password The password to login with.
     * @return The logged in user.
     */
    suspend fun login(email: String, password: String): User?

    /**
     * Register with email and password.
     *
     * @param email The email to register with.
     * @param username The username to register with.
     * @param password The password to register with.
     * @return The registered user.
     */
    suspend fun register(email: String, username: String, password: String): User?

    /**
     * Logout the current user.
     *
     * @return Whether the logout was successful.
     */
    suspend fun logout(): Boolean
}