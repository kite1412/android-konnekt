package nrr.konnekt.core.network.supabase

import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.repository.UserRepository
import nrr.konnekt.core.domain.repository.UserRepository.UserError
import nrr.konnekt.core.domain.repository.UserResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.User
import javax.inject.Inject

internal class SupabaseUserRepository @Inject constructor(
    authentication: Authentication
) : UserRepository, SupabaseService(authentication) {
    override suspend fun getUsersByUsername(username: String): UserResult<List<User>> =
        performSuspendingAuthenticatedAction {
            try {
                val res = users {
                    select {
                        filter {
                            User::username like "%$username%"
                            User::email neq it.email
                        }
                    }.decodeList<User>()
                }
                Success(res)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(UserError.Unknown)
            }
        }

    override suspend fun getUserById(id: String): UserResult<User> =
        performSuspendingAuthenticatedAction {
            try {
                val res = users {
                    select {
                        filter {
                            User::id eq id
                        }
                    }.decodeSingle<User>()
                }
                Success(res)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(UserError.Unknown)
            }
        }
}