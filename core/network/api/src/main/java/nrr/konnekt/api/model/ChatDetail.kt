package nrr.konnekt.api.model

import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.Event
import nrr.konnekt.core.model.User

data class ChatDetail(
    val chat: Chat,
    val participants: List<User>,
    val events: List<Event>
)
