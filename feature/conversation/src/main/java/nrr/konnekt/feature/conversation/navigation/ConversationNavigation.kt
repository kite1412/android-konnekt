package nrr.konnekt.feature.conversation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ConversationRoute(
    val chatId: String
)