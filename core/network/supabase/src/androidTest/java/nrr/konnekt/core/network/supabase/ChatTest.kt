package nrr.konnekt.core.network.supabase

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ChatTest : AuthSetup() {
    private val logTag = "ChatTest"

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
            chatSetting = CreateChatSetting(
                name = "test",
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

    @Test
    fun observeLatestChatMessagesSuccess() = runTest {
        val res = repo.observeLatestChatMessages().first()
        Log.d(
            logTag,
            "latest messages:\n${
                res.joinToString(separator = ", ") {
                    it.message?.content ?: "null"
                }
            }"
        )
        // potentially fail should the chats empty
        assert(res.isNotEmpty())
    }
}