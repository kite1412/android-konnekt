package nrr.konnekt.core.network.supabase

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.network.supabase.util.LOG_TAG
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FileResolverTest : TestSetup() {
    private lateinit var resolver: SupabaseFileResolver

    @Before
    override fun init() = runBlocking {
        super.init()
        initUser()
        resolver = SupabaseFileResolver()
    }

    @Test
    fun iconAndChatMediaPathRegexSuccess() {
        val iconBucketRegex = resolver.iconBucketRegex
        val chatMediaBucketRegex = resolver.chatMediaBucketRegex
        val iconPaths = listOf(
            "icon/person/anyUserId.png",
            "icon/group/anyGroupId.jpeg",
            "icon/group/anyGroupId.jpg"
        )
        val chatMediaPaths = listOf(
            "chat-media/any-chat_id/any/media.jpg",
            "chat-media/any_chat-id/a.*video.mp4"
        )

        iconPaths.forEach {
            assert(iconBucketRegex.matches(it))
        }
        chatMediaPaths.forEach {
            assert(chatMediaBucketRegex.matches(it))
        }
        listOf(iconPaths, chatMediaPaths).flatten().forEach {
            Log.d(LOG_TAG, resolver.getFileInfo(it).toString())
        }
    }

    @Test
    fun resolveImageSuccess() = runTest {
        // potentially fail if the file is not present in storage or ext doesn't match
        assert(
            resolver.resolveFile(
                "icon/group/${getProperty("SUPABASE_CHAT_ID")}.png"
            ) != null
        )
    }
}