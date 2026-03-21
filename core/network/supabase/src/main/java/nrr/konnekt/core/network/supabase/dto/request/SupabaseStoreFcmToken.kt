package nrr.konnekt.core.network.supabase.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseStoreFcmToken(
    @SerialName("user_id")
    val userId: String,
    val token: String
)