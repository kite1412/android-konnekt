package nrr.konnekt.core.domain.model

import nrr.konnekt.core.model.UserActivityStatus

data class UserPresence(
    val isActive: Boolean,
    val status: UserActivityStatus
)
