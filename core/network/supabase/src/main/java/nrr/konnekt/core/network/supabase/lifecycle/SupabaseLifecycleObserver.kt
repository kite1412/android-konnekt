package nrr.konnekt.core.network.supabase.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import nrr.konnekt.core.network.supabase.SupabaseManager

internal class SupabaseLifecycleObserver : DefaultLifecycleObserver {
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        SupabaseManager.disconnectRealtimeClient()
    }
}