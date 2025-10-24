package nrr.konnekt.core.media

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaPlayerTest {
    lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry
            .getInstrumentation()
            .context
    }

    @Test
    fun playMediaSuccess() {
        val inputStream = context.assets.open("song.mp3")
        val mediaBytes = inputStream.readBytes()
        inputStream.close()

        runOnMainThread {
            MediaPlayerManager.resumeOrPlayMedia(context, mediaBytes, "key")
        }
        assert(MediaPlayerManager.playbackState.value == PlaybackState.PLAYING)
        Thread.sleep(5000)
        runOnMainThread {
            MediaPlayerManager.resumeOrPlayMedia(context, mediaBytes, "key")
        }
    }

    private fun runOnMainThread(block: () -> Unit) = InstrumentationRegistry.getInstrumentation()
        .runOnMainSync(block)
}