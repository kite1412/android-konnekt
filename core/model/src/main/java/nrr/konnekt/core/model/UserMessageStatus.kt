package nrr.konnekt.core.model

data class UserMessageStatus(
    val user: User,
    val isDeleted: Boolean = false
)