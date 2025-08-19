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
    val chatId: String
)

fun NavController.navigateToConversation(chatId: String) =
    navigate(ConversationRoute(chatId))

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