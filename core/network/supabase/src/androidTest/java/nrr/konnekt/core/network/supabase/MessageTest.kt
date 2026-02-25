package nrr.konnekt.core.network.supabase

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import nrr.konnekt.core.domain.dto.FileUpload
import nrr.konnekt.core.domain.util.Result
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MessageTest : TestSetup() {
    lateinit var repo: SupabaseMessageRepository

    @Before
    override fun init(): Unit = runBlocking {
        super.init()
         repo = SupabaseMessageRepository(
             authentication = auth,
             fileNameFormatter = SupabaseFileNameFormatter(),
             fileUploadConstraints = SupabaseFileUploadConstraints()
        )
        initUser()
    }

    @Test
    fun sendMessageNoAttachmentsSuccess() = runTest {
        assert(
            repo.sendMessage(
                chatId = getProperty("SUPABASE_CHAT_ID"),
                content = "Hello there!"
            ) is Result.Success
        )
    }

    @Test
    fun sendMessageWithAttachmentsSuccess() = runTest {
        assert(
            repo.sendMessage(
                chatId = getProperty("SUPABASE_CHAT_ID"),
                content = "Hello there!, with an image",
                attachments = listOf(
                    FileUpload(
                        fileName = "an-image",
                        fileExtension = "png",
                        content = loadFile("an-image.png")
                    )
                )
            ) is Result.Success
        )
    }

    @Test
    fun deleteMessageSuccess() = runTest {
        assert(
            repo.deleteMessages(
                messageIds = listOf(
                    getProperty("SUPABASE_MESSAGE_ID")
                )
            ) is Result.Success
        )
    }
}