package nrr.konnekt.core.network.supabase.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import nrr.konnekt.core.network.supabase.SupabaseManager

internal class SupabaseLifecycleObserver : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        SupabaseManager.disconnectRealtimeClient()
    }
}