package nrr.konnekt.core.domain.model

import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User

data class MessageDetail(
    val sender: User,
    val message: Message
)
