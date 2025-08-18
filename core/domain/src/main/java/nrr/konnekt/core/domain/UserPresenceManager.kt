package nrr.konnekt.core.domain

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.model.UserPresence
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserStatus

typealias UserPresenceResult<T> = Result<T, UserPresenceManagerError>

/**
 * Contract for managing current user's presence
 */
interface UserPresenceManager {
    /**
     * Observe the current user's presence.
     *
     * @param userId The ID of the user to observe presence for.
     * @return A [Flow] of [UserPresence] representing the current user's presence.
     */
    fun observeUserPresence(userId: String): Flow<UserPresence?>

    /**
     * Mark the current user as active/online.
     *
     * @param user The user to mark as active.
     */
    suspend fun markUserActive(user: User): UserPresenceResult<UserStatus>

    /**
     * Mark the current user as inactive/offline.
     *
     * @param user The user to mark as inactive.
     */
    suspend fun markUserInactive(user: User): UserPresenceResult<UserStatus>

    /**
     * Update the last active time of the current user.
     *
     * @param user The user to update the last active time for.
     */
    suspend fun updateLastActiveAt(user: User): UserPresenceResult<UserStatus>

    sealed interface UserPresenceManagerError : Error {
        object Unknown : UserPresenceManagerError
    }
}