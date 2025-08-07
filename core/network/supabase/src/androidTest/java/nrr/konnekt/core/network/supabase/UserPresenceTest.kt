package nrr.konnekt.core.network.supabase

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.jan.supabase.realtime.presenceDataFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserStatus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class UserPresenceTest : AuthSetup() {
    private val logTag = "UserPresenceTest"
    private lateinit var manager: SupabaseUserPresenceManager
    private lateinit var currentUser: User

    @Before
    override fun init() = runBlocking {
        super.init()
        manager = SupabaseUserPresenceManager(auth)
        currentUser = initUser()
    }

    @Test
    fun updateUserStatusSuccess(): Unit = runBlocking {
        val job = presenceChannel.presenceDataFlow<UserStatus>()
            .onEach {
                Log.d(logTag, "presences: $it")
            }.launchIn(this)
        delay(4000)
        manager.markUserActive(currentUser)
        delay(2000)
        val res = manager.markUserInactive(currentUser)
        delay(2000)
        assert(res is Result.Success)
        job.cancel()
    }
}