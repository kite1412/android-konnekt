package nrr.konnekt.core.network.supabase.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SupabaseCreateAttachment(
    val type: String,
    val path: String,
    val name: String,
    val size: Long? = null
)
