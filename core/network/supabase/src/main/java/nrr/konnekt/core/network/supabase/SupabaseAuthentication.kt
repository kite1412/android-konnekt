package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.domain.AuthResult
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.domain.Authentication.AuthError
import nrr.konnekt.core.domain.util.Error
import nrr.konnekt.core.domain.util.Success
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.supabase.util.Tables.USERS
import nrr.konnekt.core.network.supabase.util.toUser
import javax.inject.Inject

internal class SupabaseAuthentication @Inject constructor() : Authentication {
    private val client: SupabaseClient = supabaseClient
    private val _loggedInUser = MutableStateFlow(client.auth.currentUserOrNull()?.toUser())
    override val loggedInUser: Flow<User?>
        get() = _loggedInUser.asStateFlow()

    override fun getLoggedInUserOrNull(): User? = _loggedInUser.value

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
                    } else _loggedInUser.value = user
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
}