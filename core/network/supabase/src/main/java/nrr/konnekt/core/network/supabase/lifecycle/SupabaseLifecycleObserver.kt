package nrr.konnekt.core.network.supabase.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class SupabaseLifecycleObserver : DefaultLifecycleObserver {
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
//        SupabaseManager.disconnectRealtimeClient()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
//        SupabaseManager.connectRealtimeClient()
    }
}