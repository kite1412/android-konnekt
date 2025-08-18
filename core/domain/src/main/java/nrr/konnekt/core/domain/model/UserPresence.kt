package nrr.konnekt.core.domain.model

import nrr.konnekt.core.model.UserStatus

data class UserPresence(
    val isActive: Boolean,
    val status: UserStatus
)
