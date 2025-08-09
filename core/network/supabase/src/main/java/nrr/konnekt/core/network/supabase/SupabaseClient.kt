package nrr.konnekt.core.network.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.storage.Storage
import io.ktor.http.headers

internal val supabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Realtime)
    install(Storage)

    headers {
        append("Accept-Timezone", "UTC")
    }
}

internal val presenceChannel = supabaseClient
    .channel("user-presence") {
        supabaseClient.auth.currentUserOrNull()?.let {
            presence {
                key = it.id
            }
        }
    }