package nrr.konnekt.core.network.supabase

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.domain.dto.CreateChatSetting
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.util.Result
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.User
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ChatTest : TestSetup() {
    private val logTag = "ChatTest"

    private lateinit var repo: SupabaseChatRepository
    private lateinit var userPresenceManager: SupabaseUserPresenceManager
    private lateinit var user: User

    @Before
    override fun init(): Unit = runBlocking {
        super.init()
        user = initUser()
        userPresenceManager = SupabaseUserPresenceManager(auth)
        repo = SupabaseChatRepository(
            authentication = auth,
            userPresenceManager = userPresenceManager
        )
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
            participantIds = listOf(getProperty("SUPABASE_CHAT_ID"), "mock-id")
        )
        assert(res is Result.Error)
    }

    @Test
    fun createGroupChatSuccess() = runTest {
        val res = repo.createChat(
            type = ChatType.GROUP,
            chatSetting = CreateChatSetting(
                name = "new group with icon",
                permissionSettings = ChatPermissionSettings(),
                icon = FileUpload(
                    fileName = "any.png",
                    fileExtension = "png",
                    content = loadFile("konnekt-icon.png")
                )
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
                    "${it.message?.message?.content}, " +
                            "sender: ${it.message?.sender?.username}"
                }
            }"
        )
        // potentially fail should the chats empty
        assert(res.isNotEmpty())
    }

    @Test
    fun observeActiveParticipantsSuccess() = runBlocking {
        userPresenceManager.updateLastActiveAt(user)
        userPresenceManager.markUserActive(user)
        val job = repo
            .observeActiveParticipants(getProperty("SUPABASE_CHAT_ID"))
            .onEach {
                Log.d(logTag, "active participants: $it")
            }.launchIn(this)

        delay(5000)
        job.cancel()
    }
}