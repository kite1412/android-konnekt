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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nrr.konnekt.core.common.annotation.AppCoroutineScope
import nrr.konnekt.core.common.result.Error
import nrr.konnekt.core.common.result.Result
import nrr.konnekt.core.common.result.Success
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.domain.UserPresenceManager.UserPresenceManagerError
import nrr.konnekt.core.domain.UserPresenceResult
import nrr.konnekt.core.domain.model.UserPresence
import nrr.konnekt.core.model.UserActivityStatus
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUserActivityStatus
import nrr.konnekt.core.network.supabase.dto.response.toModel
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import nrr.konnekt.core.network.supabase.util.updateSupabaseUserActivityStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SupabaseUserPresenceManager @Inject constructor(
    @param:AppCoroutineScope private val scope: CoroutineScope,
    private val authentication: Authentication,
    private val userRepository: SupabaseUserRepository
) : UserPresenceManager, SupabaseService(authentication) {
    private val _activeUsers  = MutableStateFlow<List<SupabaseUserActivityStatus>>(emptyList())
    val activeUsers = _activeUsers.asStateFlow()

    private suspend fun <T> checkRealtimeConnection(
        onConnected: suspend () -> UserPresenceResult<T>
    ): UserPresenceResult<T> {
        val connectionStatus = supabaseClient.realtime.status.value

        return if (connectionStatus == Realtime.Status.CONNECTED) {
            onConnected()
        } else Error(UserPresenceManagerError.Unknown)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUserPresence(userId: String): Flow<UserPresence?> {
        return activeUsers
            .flatMapLatest { userStatuses ->
                flowOf(
                    userActivityStatuses {
                        select {
                            filter {
                                SupabaseUserActivityStatus::userId eq userId
                            }
                        }.decodeSingleOrNull<SupabaseUserActivityStatus>()
                            ?.let {
                                val res = userRepository.getUserById(userId)

                                if (res is Result.Success) it.toModel(res.data)
                                else null
                            }
                    }
                        ?.let {
                            UserPresence(
                                isActive = userStatuses.firstOrNull { u -> u.userId == userId } != null,
                                status = it
                            )
                        }
                )
            }
    }

    override suspend fun markUserActive(): UserPresenceResult<UserActivityStatus> =
        authentication.getLoggedInUserOrNull()?.let {
            checkRealtimeConnection {
                val userStatus = it.updateSupabaseUserActivityStatus()
                if (presenceChannel.status.value != RealtimeChannel.Status.SUBSCRIBED) {
                    presenceChannel
                        .presenceDataFlow<SupabaseUserActivityStatus>()
                        .onEach { l ->
                            Log.d(LOG_TAG, "active users: $l")
                            _activeUsers.value = l
                        }
                        .launchIn(scope)
                    updateLastActiveAt()
                    presenceChannel.subscribe(true)
                    presenceChannel.track(userStatus)
                }
                Success(userStatus.toModel(it))
            }
        } ?: Error(UserPresenceManagerError.Unauthenticated)

    override suspend fun markUserInactive(): UserPresenceResult<UserActivityStatus> =
        authentication.getLoggedInUserOrNull()?.let {
            checkRealtimeConnection {
                val userStatus = it.updateSupabaseUserActivityStatus()
                presenceChannel.untrack()
                presenceChannel.unsubscribe()
                updateLastActiveAt()
                Log.d(LOG_TAG, "marked inactive")
                Success(userStatus.toModel(it))
            }
        } ?: Error(UserPresenceManagerError.Unauthenticated)

    override suspend fun updateLastActiveAt(): UserPresenceResult<UserActivityStatus> =
        authentication.getLoggedInUserOrNull()?.let {
            userActivityStatuses {
                upsert(it.updateSupabaseUserActivityStatus()) {
                    select()
                }.decodeSingleOrNull<SupabaseUserActivityStatus>()
            }?.let { s -> Success(s.toModel(it)) }
        } ?: Error(UserPresenceManagerError.Unknown)
}