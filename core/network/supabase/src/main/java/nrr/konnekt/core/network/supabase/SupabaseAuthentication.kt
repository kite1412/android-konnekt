package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.AuthResult
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.Authentication.AuthError
import nrr.konnekt.core.domain.util.AuthStatus
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import nrr.konnekt.core.network.supabase.util.Tables.USERS
import nrr.konnekt.core.network.supabase.util.toUser
import javax.inject.Inject

internal class SupabaseAuthentication @Inject constructor() : Authentication {
    private val client: SupabaseClient = supabaseClient
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Loading)
    private val _loggedInUser = MutableStateFlow(client.auth.currentUserOrNull()?.toUser())

    override val authStatus: Flow<AuthStatus>
        get() = _authStatus.asStateFlow()
    override val loggedInUser: Flow<User?>
        get() = _loggedInUser.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            client.auth.sessionStatus
                .onEach {
                    when (it) {
                        is SessionStatus.Authenticated -> {
                            if (
                                it.source !is SessionSource.SignIn
                                && _loggedInUser.value == null
                            ) {
                                val user = client.auth
                                    .currentUserOrNull()
                                    ?.toUser()
                                    ?.let { u ->
                                        client.postgrest.from(USERS).select {
                                            filter {
                                                User::id eq u.id
                                            }
                                        }.decodeSingleOrNull<User>()
                                    }
                                logCurrentUser(user)
                                if (user != null) {
                                    _loggedInUser.value = user
                                    _authStatus.value = AuthStatus.Authenticated(user)
                                } else {
                                    _authStatus.value = AuthStatus.Unauthenticated
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
                .launchIn(this)
        }
    }

    private fun logCurrentUser(user: User?) = Log.d(LOG_TAG, "Current user: $user")

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
            signedInUser?.let {
                client.postgrest.from(USERS).apply {
                    val user = select {
                        filter {
                            eq("id", it.id)
                        }
                    }.decodeSingleOrNull<User>()

                    if (user == null) {
                        val new = User(
                            id = it.id,
                            email = it.email,
                            imagePath = null,
                            username = it.username,
                            bio = null,
                            createdAt = it.createdAt
                        )
                        insert(new)
                        _loggedInUser.value = new
                        _authStatus.value = AuthStatus.Authenticated(new)
                        logCurrentUser(new)
                    } else {
                        _loggedInUser.value = user
                        _authStatus.value = AuthStatus.Authenticated(user)
                        logCurrentUser(user)
                    }
                }
            }
            return _loggedInUser.value?.let {
                Success(it)
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
            }.decodeSingleOrNull<User>()
            if (existingUser == null) {
                val user = client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = buildJsonObject {
                        put("username", username)
                    }
                }
                return user?.toUser()?.let {
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
            return Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            return Error(AuthError.Unknown)
        }
    }

    override suspend fun updateCurrentUser(user: User) {
        _loggedInUser.value = user
    }
}