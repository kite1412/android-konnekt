package nrr.konnekt.core.network.supabase

import androidx.test.platform.app.InstrumentationRegistry
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User
import org.junit.Before
import java.util.Properties

internal abstract class AuthSetup {
    protected lateinit var properties: Properties
    protected lateinit var auth: SupabaseAuthentication

    protected fun getProperty(key: String): String {
        return properties.getProperty(key)
    }

    @Before
    open fun init() {
        properties = Properties()
        val inputStream = InstrumentationRegistry
            .getInstrumentation()
            .context
            .assets
            .open("secret.properties")
        properties.load(inputStream)
        auth = SupabaseAuthentication()
    }

    suspend fun initUser(): User = with(
        auth.login(
            email = getProperty("SUPABASE_EMAIL"),
            password = getProperty("SUPABASE_PASSWORD")
        )
    ) {
        if (this is Result.Success) data
        else throw Exception("Could not initialize user")
    }
}