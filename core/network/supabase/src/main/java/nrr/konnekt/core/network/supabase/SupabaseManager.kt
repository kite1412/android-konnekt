package nrr.konnekt.core.network.supabase

import android.util.Log
import io.github.jan.supabase.realtime.realtime
import nrr.konnekt.core.network.supabase.util.LOG_TAG

object SupabaseManager {
    fun disconnectRealtimeClient() {
        Log.d(LOG_TAG, "Disconnecting supabase realtime client")
        supabaseClient.realtime.disconnect()
    }
}