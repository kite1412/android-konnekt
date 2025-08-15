package nrr.konnekt.core.domain.repository

import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User

typealias UserResult<T> = Result<T, UserRepository.UserError>

/**
 * Contract for user repository.
 */
interface UserRepository {
    /**
     * Get a list of users by username.
     *
     * @param username The username to search for.
     * @return A list of users that exactly or partially match the search.
     */
    suspend fun getUsersByUsername(username: String): UserResult<List<User>>

    sealed interface UserError : Error {
        object Unknown : UserError
    }
}