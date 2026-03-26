package nrr.konnekt.core.network.supabase

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.common.annotation.AppCoroutineScope
import nrr.konnekt.core.common.manager.AppVisibilityManager
import nrr.konnekt.core.common.result.Error
import nrr.konnekt.core.common.result.Success
import nrr.konnekt.core.domain.AuthResult
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.Authentication.AuthError
import nrr.konnekt.core.domain.util.AuthStatus
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUser
import nrr.konnekt.core.network.supabase.dto.response.toModel
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import nrr.konnekt.core.network.supabase.util.Tables.USERS
import nrr.konnekt.core.network.supabase.util.toUser
import nrr.konnekt.core.storage.datastore.PreferencesKeys
import nrr.konnekt.core.storage.datastore.clearPreferences
import nrr.konnekt.core.storage.datastore.getPreference
import nrr.konnekt.core.storage.datastore.setPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SupabaseAuthentication @Inject constructor(
    appVisibilityManager: AppVisibilityManager,
    @param:ApplicationContext private val context: Context,
    @param:AppCoroutineScope private val appScope: CoroutineScope
) : Authentication {
    private val client: SupabaseClient = supabaseClient
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Loading)
    private var _loggedInUser = MutableStateFlow(client.auth.currentUserOrNull()?.toUser())

    override val authStatus: Flow<AuthStatus>
        get() = _authStatus.asStateFlow()
    override val loggedInUser: Flow<User?>
        get() = _loggedInUser.asStateFlow()

    init {
        combine(
            flow = client.auth.sessionStatus,
            flow2 = appVisibilityManager.isForeground
        ) { sessionStatus, isForeground ->
            when (sessionStatus) {
                is SessionStatus.Authenticated -> {
                    if (
                        sessionStatus.source !is SessionSource.SignIn
                        && _loggedInUser.value == null
                    ) {
                        val user = client.auth
                            .currentUserOrNull()
                            ?.also { u ->
                                if (isForeground) observeLoggedInUser(u.id)
                            }
                        if (user == null) _authStatus.value = AuthStatus.Unauthenticated
                        else {
                            val user = user.toUser()
                            _loggedInUser.value = user
                            if (!isForeground)
                                _authStatus.value = AuthStatus.Authenticated(user)
                        }
                    }
                }
                is SessionStatus.NotAuthenticated -> {
                    _loggedInUser.value = null
                    _authStatus.value = AuthStatus.Unauthenticated
                }
                is SessionStatus.RefreshFailure -> {
                    _loggedInUser.value = null
                    _authStatus.value = AuthStatus.Unauthenticated
                }
                else -> {}
            }
        }
            .launchIn(appScope)
    }

    override fun getLoggedInUserOrNull(): User? =
        _loggedInUser.value

    override suspend fun login(
        email: String,
        password: String
    ): AuthResult<User> {
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val signedInUser = client.auth.currentUserOrNull()?.toUser()
            var model: User? = null
            signedInUser?.let {
                client.postgrest.from(USERS).apply {
                    val user = select {
                        filter {
                            eq("id", it.id)
                        }
                    }.decodeSingleOrNull<SupabaseUser>()

                    if (user == null) {
                        val new = SupabaseUser(
                            id = it.id,
                            email = it.email,
                            imagePath = null,
                            username = it.username,
                            bio = null,
                            createdAt = it.createdAt
                        )
                        insert(new)
                        model = new.toModel()
                    } else {
                        model = user.toModel()
                    }
                    logCurrentUser(model)
                }
            }
            return model?.let { model ->
                appScope.launch {
                    storeFcmToken()
                    context.setPreference(
                        key = PreferencesKeys.CURRENT_USER_ID,
                        value = model.id
                    )
                }
                _loggedInUser.value = model
                _authStatus.value = AuthStatus.Authenticated(model)
                Success(model)
            } ?: Error(AuthError.Unknown)
        } catch (e: AuthRestException) {
            e.printStackTrace()
            return Error(
                when (e.errorCode) {
                    AuthErrorCode.EmailNotConfirmed -> AuthError.EmailNotConfirmed
                    else -> AuthError.InvalidCredentials
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return Error(AuthError.Unknown)
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): AuthResult<User> {
        try {
            val existingUser = client.postgrest.from(USERS).select {
                filter {
                    eq("email", email)
                }
            }.decodeSingleOrNull<SupabaseUser>()
            if (existingUser == null) {
                val user = client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = buildJsonObject {
                        put("username", username)
                    }
                }
                return user?.toUser()?.let {
                    _loggedInUser.value = it
                    storeFcmToken()
                    Success(it)
                } ?: Error(AuthError.Unknown)
            }
            return Error(AuthError.UserAlreadyExists)
        } catch (e: Exception) {
            e.printStackTrace()
            return Error(AuthError.UserAlreadyExists)
        }
    }

    override suspend fun logout(): AuthResult<Boolean> {
        try {
            client.auth.signOut()
            client.auth.currentUserOrNull()?.run {
                return Error(AuthError.Unknown)
            }
            _loggedInUser.value = null
            context.clearPreferences()
            return Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            return Error(AuthError.Unknown)
        }
    }

    override suspend fun storeFcmToken(token: String): AuthResult<Boolean> =
        getLoggedInUserOrNull()?.let {
            try {
                client.postgrest.rpc(
                    function = "store_fcm_token",
                    parameters = buildJsonObject {
                        put("_token", token)
                    }
                )
                Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(AuthError.Unknown)
            }
        } ?: Error(AuthError.UnauthenticatedAction)

    private suspend fun storeFcmToken() {
        context.getPreference(
            key = PreferencesKeys.FCM_TOKEN
        )?.let { token ->
            storeFcmToken(token)
            Log.d(LOG_TAG, "Stored FCM token successfully")
        }
    }

    @OptIn(SupabaseExperimental::class)
    private fun observeLoggedInUser(userId: String) {
        try {
            client.postgrest.from(USERS).selectSingleValueAsFlow(
                primaryKey = SupabaseUser::id
            ) {
                SupabaseUser::id eq userId
            }
                .onEach {
                    val user = it.toModel()
                    _loggedInUser.value = user
                    _authStatus.value = AuthStatus.Authenticated(user)
                    logCurrentUser(user)
                }
                .launchIn(appScope)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logCurrentUser(user: User?) = Log.d(LOG_TAG, "Current user: $user")
}