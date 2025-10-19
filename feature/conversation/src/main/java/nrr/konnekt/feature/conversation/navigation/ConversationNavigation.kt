package nrr.konnekt.feature.conversation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.Chat
import nrr.konnekt.feature.conversation.ConversationScreen

@Serializable
data class ConversationRoute(
    val chatId: String? = null,
    val peerId: String? = null
)

fun NavController.navigateToConversation(chatId: String) =
    navigate(ConversationRoute(chatId = chatId))

fun NavController.navigateToTempPersonalConversation(peerId: String) =
    navigate(ConversationRoute(peerId = peerId))

fun NavGraphBuilder.conversationScreen(
    navigateBack: () -> Unit,
    navigateToChatDetail: (Chat) -> Unit,
    contentPadding: PaddingValues
) {
    composable<ConversationRoute> {
        ConversationScreen(
            navigateBack = navigateBack,
            navigateToChatDetail = navigateToChatDetail,
            contentPadding = contentPadding
        )
    }
}