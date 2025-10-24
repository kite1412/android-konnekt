package nrr.konnekt.core.media.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import nrr.konnekt.core.media.MediaPlayerManager

internal class MediaPlayerObserver : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        MediaPlayerManager.release()
    }
}