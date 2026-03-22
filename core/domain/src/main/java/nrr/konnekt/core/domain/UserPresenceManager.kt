package nrr.konnekt.core.domain

import kotlinx.coroutines.flow.Flow
import nrr.konnekt.core.common.result.Error
import nrr.konnekt.core.common.result.Result
import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.model.UserPresence
import nrr.konnekt.core.model.UserActivityStatus

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
     * @return an updated [UserActivityStatus] of current user
     */
    suspend fun markUserActive(): UserPresenceResult<UserActivityStatus>

    /**
     * Mark the current user as inactive/offline.
     *
     * @return an updated [UserActivityStatus] of current user
     */
    suspend fun markUserInactive(): UserPresenceResult<UserActivityStatus>

    /**
     * Update the last active time of the current user.
     *
     * @return an updated [UserActivityStatus] of current user
     */
    suspend fun updateLastActiveAt(): UserPresenceResult<UserActivityStatus>

    sealed interface UserPresenceManagerError : Error {
        object Unknown : UserPresenceManagerError
        object Unauthenticated : UserPresenceManagerError
    }
}