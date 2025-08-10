package nrr.konnekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import nrr.konnekt.core.network.supabase.SupabaseManager
import nrr.konnekt.ui.KonnektApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KonnektApp()
        }
    }

    override fun onPause() {
        super.onPause()
        // TODO abstract this calling
        SupabaseManager.disconnectRealtimeClient()
    }
}