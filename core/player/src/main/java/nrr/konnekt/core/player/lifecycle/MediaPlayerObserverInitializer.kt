package nrr.konnekt.core.player.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner

internal class MediaPlayerObserverInitializer : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        if (p0 is LifecycleOwner)
            p0.lifecycle.addObserver(MediaPlayerObserver())
    }

    override fun onActivityDestroyed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}
}