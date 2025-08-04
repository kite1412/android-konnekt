package nrr.konnekt.core.network.supabase

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ChatTest : AuthSetup() {
    private lateinit var repo: SupabaseChatRepository

    @Before
    override fun init() = runBlocking {
        super.init()
        initUser()
        repo = SupabaseChatRepository(auth)
    }

    @Test
    fun createPersonalChatSuccess() = runTest {
        val res = repo.createChat(
            type = ChatType.PERSONAL,
            participantIds = listOf(getProperty("SUPABASE_USER_ID"))
        )
        assert(res !is Result.Error)
    }

    @Test
    fun createPersonalChatError() = runTest {
        val res = repo.createChat(
            type = ChatType.PERSONAL,
            participantIds = listOf(getProperty("SUPABASE_USER_ID"), "mock-id")
        )
        assert(res is Result.Error)
    }

    @Test
    fun createGroupChatSuccess() = runTest {
        val res = repo.createChat(
            type = ChatType.GROUP,
            chatSetting = ChatSetting(
                name = "test",
                iconPath = null,
                description = null,
                permissionSettings = ChatPermissionSettings()
            )
        )
        assert(res !is Result.Error)
    }

    @Test
    fun createGroupChatFail() = runTest {
        val res = repo.createChat(
            type = ChatType.GROUP
        )
        assert(res is Result.Error)
    }
}