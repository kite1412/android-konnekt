package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.api.Authentication
import nrr.konnekt.core.network.supabase.util.toUser
import javax.inject.Inject

class SupabaseAuthentication @Inject constructor () : Authentication {
    private val client: SupabaseClient = supabaseClient
    private val _loggedInUser = MutableStateFlow(client.auth.currentUserOrNull()?.toUser())
    override val loggedInUser: Flow<User?>
        get() = _loggedInUser.asStateFlow()

    override suspend fun login(
        email: String,
        password: String
    ): User? {
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            _loggedInUser.value = client.auth.currentUserOrNull()?.toUser()
            return _loggedInUser.value
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): User? {
        try {
            val user = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("username", username)
                }
            }
            return user?.toUser()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun logout(): Boolean {
        try {
            client.auth.signOut()
            client.auth.currentUserOrNull()?.run {
                return false
            }
            _loggedInUser.value = null
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}