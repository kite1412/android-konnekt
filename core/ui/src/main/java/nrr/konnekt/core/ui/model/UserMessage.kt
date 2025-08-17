package nrr.konnekt.core.ui.model

import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User

data class UserMessage(
    val user: User,
    val message: Message
)