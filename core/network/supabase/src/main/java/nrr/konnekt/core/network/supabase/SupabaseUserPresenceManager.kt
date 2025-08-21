package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.presenceDataFlow
import io.github.jan.supabase.realtime.track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.UserPresenceResult
import nrr.konnekt.core.domain.model.UserPresence
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserStatus
import nrr.konnekt.core.model.updateUserStatus
import javax.inject.Inject
import kotlin.time.Instant

internal class SupabaseUserPresenceManager @Inject constructor(
    private val authentication: Authentication
) : UserPresenceManager, SupabaseService(authentication) {
    private val _activeUsers: MutableStateFlow<List<UserStatus>> = MutableStateFlow(emptyList())
    val activeUsers = _activeUsers.asStateFlow()
    override fun observeUserPresence(userId: String): Flow<UserPresence?> =
        channelFlow {
            if (presenceChannel.status.value != RealtimeChannel.Status.SUBSCRIBED)
                authentication.getLoggedInUserOrNull()?.let {
                    markUserActive(it)
                }

            _activeUsers
                .onEach { userStatuses ->
                    trySend(
                        UserPresence(
                            isActive = userStatuses.firstOrNull { u -> u.userId == userId } != null,
                            status = userStatuses {
                                select {
                                    filter {
                                        UserStatus::userId
                                    }
                                }.decodeSingleOrNull<UserStatus>()
                            } ?: UserStatus(
                                userId = userId,
                                lastActiveAt = Instant.DISTANT_PAST
                            )
                        )
                    )
                }
                .launchIn(this)
        }

    override suspend fun markUserActive(user: User): UserPresenceResult<UserStatus> {
        val userStatus = user.updateUserStatus()
        if (presenceChannel.status.value != RealtimeChannel.Status.SUBSCRIBED) {
            CoroutineScope(Dispatchers.Default).launch {
                presenceChannel
                    .presenceDataFlow<UserStatus>()
                    .onEach {
                        _activeUsers.value = it
                    }
                    .launchIn(this)
            }
            presenceChannel.subscribe(true)
            presenceChannel.track(userStatus)
            updateLastActiveAt(user)
        }
        return Success(userStatus)
    }

    override suspend fun markUserInactive(user: User): UserPresenceResult<UserStatus> {
        val userStatus = user.updateUserStatus()
        presenceChannel.untrack()
        presenceChannel.unsubscribe()
        updateLastActiveAt(user)
        return Success(userStatus)
    }

    override suspend fun updateLastActiveAt(user: User): UserPresenceResult<UserStatus> =
        userStatuses {
            upsert(user.updateUserStatus()) {
                select()
            }.decodeSingleOrNull<UserStatus>()
        }?.let { Success(it) } ?: Error(UserPresenceManagerError.Unknown)
}