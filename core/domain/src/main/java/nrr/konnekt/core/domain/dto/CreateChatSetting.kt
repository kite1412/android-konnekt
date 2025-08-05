package nrr.konnekt.core.domain.dto

import nrr.konnekt.core.model.ChatPermissionSettings

data class CreateChatSetting(
    val name: String,
    val description: String? = null,
    val icon: ByteArray? = null,
    val permissionSettings: ChatPermissionSettings
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateChatSetting

        if (name != other.name) return false
        if (description != other.description) return false
        if (!icon.contentEquals(other.icon)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (icon?.contentHashCode() ?: 0)
        return result
    }
}