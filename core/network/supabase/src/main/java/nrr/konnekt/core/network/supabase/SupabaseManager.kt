package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nrr.konnekt.core.network.supabase.util.LOG_TAG

internal object SupabaseManager {
    fun disconnectRealtimeClient() {
        Log.d(LOG_TAG, "Disconnecting supabase realtime client")
        supabaseClient.realtime.disconnect()
    }

    fun connectRealtimeClient() {
        CoroutineScope(Dispatchers.Default).launch {
            Log.d(LOG_TAG, "Connecting supabase realtime client")
            supabaseClient.realtime.connect()
        }
    }
}