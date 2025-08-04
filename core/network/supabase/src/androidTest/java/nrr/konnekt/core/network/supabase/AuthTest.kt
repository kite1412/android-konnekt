package nrr.konnekt.core.network.supabase

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.domain.util.Result
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Properties

@RunWith(AndroidJUnit4::class)
class AuthTest {
    private lateinit var auth: SupabaseAuthentication
    private lateinit var properties: Properties

    private fun getProperty(key: String): String {
        return properties.getProperty(key)
    }

    private suspend fun login() {
        assert(auth.loggedInUser.first() == null)
        val email = getProperty("SUPABASE_EMAIL")
        val password = getProperty("SUPABASE_PASSWORD")
        val res = auth.login(email, password)
        assert(res is Result.Success && auth.loggedInUser.first() == res.data)
        assert(auth.loggedInUser.first() != null)
    }

    @Before
    fun setup() {
        properties = Properties()
        val inputStream = InstrumentationRegistry
            .getInstrumentation()
            .context
            .assets
            .open("secret.properties")
        properties.load(inputStream)
        auth = SupabaseAuthentication()
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