package nrr.konnekt.feature.chatdetail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.feature.chatdetail.ChatDetailScreen

@Serializable
data class ChatDetailRoute(
    val chatId: String? = null,
    val peerId: String? = null
)

fun NavController.navigateToChatDetail(chatId: String) =
    navigate(ChatDetailRoute(chatId = chatId))

fun NavController.navigateToTempPersonalChatDetail(peerId: String) =
    navigate(ChatDetailRoute(peerId = peerId))

fun NavGraphBuilder.chatDetailScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    // isChatId == false: use peer id instead
    navigateToConversation: (isChatId: Boolean, id: String) -> Unit,
    contentPadding: PaddingValues
) {
    composable<ChatDetailRoute> {
        ChatDetailScreen(
            navController = navController,
            navigateBack = navigateBack,
            navigateToConversation = navigateToConversation,
            contentPadding = contentPadding
        )
    }
}