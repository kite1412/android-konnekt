package nrr.konnekt.core.network.supabase.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChat(
    val type: String
)
