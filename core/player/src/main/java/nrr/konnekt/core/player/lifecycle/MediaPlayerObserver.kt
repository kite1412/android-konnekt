package nrr.konnekt.core.player.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import nrr.konnekt.core.player.MediaPlayerManager

internal class MediaPlayerObserver : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        MediaPlayerManager.release()
    }
}