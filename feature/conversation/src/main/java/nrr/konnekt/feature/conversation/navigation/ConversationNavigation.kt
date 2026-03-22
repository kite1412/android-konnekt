package nrr.konnekt.feature.conversation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import nrr.konnekt.core.notification.util.KonnektNotification
import nrr.konnekt.feature.conversation.ConversationScreen
import nrr.konnekt.feature.conversation.util.IdType

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
    navController: NavController,
    navigateBack: () -> Unit,
    navigateToChatDetail: (id: String, idType: IdType) -> Unit,
    contentPadding: PaddingValues
) {
    composable<ConversationRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = KonnektNotification.Messages.DEEP_LINK_URI_PATTERN
            }
        )
    ) {
        ConversationScreen(
            navController = navController,
            navigateBack = navigateBack,
            navigateToChatDetail = navigateToChatDetail,
            contentPadding = contentPadding
        )
    }
}