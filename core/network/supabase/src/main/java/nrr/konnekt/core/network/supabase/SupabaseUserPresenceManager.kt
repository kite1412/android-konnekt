package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.realtime.track
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.UserPresenceResult
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserStatus
import nrr.konnekt.core.model.updateUserStatus
import javax.inject.Inject

internal class SupabaseUserPresenceManager @Inject constructor(
    authentication: Authentication
) : UserPresenceManager, SupabaseService(authentication) {
    override suspend fun markUserActive(user: User): UserPresenceResult {
        val userStatus = user.updateUserStatus()
        presenceChannel.subscribe(true)
        presenceChannel.track(userStatus)
        return Success(userStatus)
    }

    override suspend fun markUserInactive(user: User): UserPresenceResult {
        val userStatus = user.updateUserStatus()
        presenceChannel.untrack()
        presenceChannel.unsubscribe()
        return Success(userStatus)
    }

    override suspend fun updateLastActiveAt(user: User): UserPresenceResult =
        userStatuses {
            upsert(user.updateUserStatus()) {
                select()
            }.decodeSingleOrNull<UserStatus>()
        }?.let { Success(it) } ?: Error(UserPresenceManagerError.Unknown)
}