package nrr.konnekt.core.network.supabase

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.domain.util.Result
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class AuthTest : TestSetup() {
    private suspend fun login() {
        assert(auth.loggedInUser.first() == null)
        val email = getProperty("SUPABASE_EMAIL")
        val password = getProperty("SUPABASE_PASSWORD")
        val res = auth.login(email, password)
        assert(res is Result.Success && auth.loggedInUser.first() == res.data)
        assert(auth.loggedInUser.first() != null)
    }

    @Test
    fun loginSuccess() = runTest {
        login()
    }

    @Test
    fun registerSuccess() = runTest {
        assert(auth.loggedInUser.first() == null)
        val email = getProperty("SUPABASE_NEW_EMAIL")
        val username = getProperty("SUPABASE_NEW_USERNAME")
        val password = getProperty("SUPABASE_PASSWORD")
        assert(auth.register(email, username, password) is Result.Success)
    }

    @Test
    fun logoutSuccess() = runTest {
        login()
        assert(auth.logout() is Result.Success)
        assert(auth.loggedInUser.first() == null)
    }
}