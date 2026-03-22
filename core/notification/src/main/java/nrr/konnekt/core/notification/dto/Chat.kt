package nrr.konnekt.core.notification.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String,
    val name: String?,
    @SerialName("is_group")
    val isGroup: Boolean,
    @SerialName("icon_path")
    val iconPath: String?,
    val participants: List<Participant>
)
