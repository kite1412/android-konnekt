package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.presenceDataFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.UserPresenceResult
import nrr.konnekt.core.domain.annotation.AppCoroutineScope
import nrr.konnekt.core.domain.model.UserPresence
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.UserStatus
import nrr.konnekt.core.model.updateUserStatus
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import javax.inject.Inject
import kotlin.time.Instant

internal class SupabaseUserPresenceManager @Inject constructor(
    @param:AppCoroutineScope private val scope: CoroutineScope,
    private val authentication: Authentication
) : UserPresenceManager, SupabaseService(authentication) {
    private val _activeUsers  = MutableStateFlow<List<UserStatus>>(emptyList())
    val activeUsers = _activeUsers.asStateFlow()

    private suspend fun <T> checkRealtimeConnection(
        onConnected: suspend () -> UserPresenceResult<T>
    ): UserPresenceResult<T> {
        val connectionStatus = supabaseClient.realtime.status.firstOrNull()

        return if (connectionStatus != null && connectionStatus == Realtime.Status.CONNECTED) {
            onConnected()
        } else Error(UserPresenceManagerError.Unknown)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUserPresence(userId: String): Flow<UserPresence?> {
        return activeUsers
            .flatMapLatest { userStatuses ->
                flowOf(
                    UserPresence(
                        isActive = userStatuses.firstOrNull { u -> u.userId == userId } != null,
                        status = userStatuses {
                            select {
                                filter {
                                    UserStatus::userId eq userId
                                }
                            }.decodeSingleOrNull<UserStatus>()
                        } ?: UserStatus(
                            userId = userId,
                            lastActiveAt = Instant.DISTANT_PAST
                        )
                    )
                )
            }
    }

    override suspend fun markUserActive(): UserPresenceResult<UserStatus> =
        authentication.getLoggedInUserOrNull()?.let {
            checkRealtimeConnection {
                val userStatus = it.updateUserStatus()
                if (presenceChannel.status.value != RealtimeChannel.Status.SUBSCRIBED) {
                    presenceChannel
                        .presenceDataFlow<UserStatus>()
                        .onEach { l ->
                            Log.d(LOG_TAG, "active users: $l")
                            _activeUsers.value = l
                        }
                        .launchIn(scope)
                    updateLastActiveAt()
                    presenceChannel.subscribe(true)
                    presenceChannel.track(userStatus)
                }
                Success(userStatus)
            }
        } ?: Error(UserPresenceManagerError.Unauthenticated)

    override suspend fun markUserInactive(): UserPresenceResult<UserStatus> =
        authentication.getLoggedInUserOrNull()?.let {
            checkRealtimeConnection {
                val userStatus = it.updateUserStatus()
                presenceChannel.untrack()
                presenceChannel.unsubscribe()
                updateLastActiveAt()
                Success(userStatus)
            }
        } ?: Error(UserPresenceManagerError.Unauthenticated)

    override suspend fun updateLastActiveAt(): UserPresenceResult<UserStatus> =
        authentication.getLoggedInUserOrNull()?.let {
            userStatuses {
                upsert(it.updateUserStatus()) {
                    select()
                }.decodeSingleOrNull<UserStatus>()
            }?.let { s -> Success(s) }
        } ?: Error(UserPresenceManagerError.Unknown)
}