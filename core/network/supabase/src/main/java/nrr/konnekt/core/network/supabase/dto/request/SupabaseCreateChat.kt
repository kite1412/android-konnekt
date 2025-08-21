package nrr.konnekt.core.network.supabase.dto.request

import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseCreateChat(
    val type: String
)
