package nrr.konnekt.feature.conversation.util

import nrr.konnekt.core.model.User
import kotlin.time.Instant

internal data class UserReadMarker(
    val user: User,
    val lastReadAt: Instant?
)
