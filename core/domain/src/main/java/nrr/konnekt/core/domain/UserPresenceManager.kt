package nrr.konnekt.core.domain

import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserStatus

typealias UserPresenceResult = Result<UserStatus, UserPresenceManagerError>

/**
 * Contract for managing current user's presence
 */
interface UserPresenceManager {
    /**
     * Mark the current user as active/online.
     *
     * @param user The user to mark as active.
     */
    suspend fun markUserActive(user: User): UserPresenceResult

    /**
     * Mark the current user as inactive/offline.
     *
     * @param user The user to mark as inactive.
     */
    suspend fun markUserInactive(user: User): UserPresenceResult

    /**
     * Update the last active time of the current user.
     *
     * @param user The user to update the last active time for.
     */
    suspend fun updateLastActiveAt(user: User): UserPresenceResult

    sealed interface UserPresenceManagerError : Error {
        object Unknown : UserPresenceManagerError
    }
}