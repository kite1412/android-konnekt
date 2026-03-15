package nrr.konnekt.feature.chats.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable
import nrr.konnekt.feature.chats.ChatsScreen

@Serializable
object ChatsRoute

fun NavController.navigateToChats(
    navOptionsBuilder: (NavOptionsBuilder.() -> Unit)? = null
) = navigate(
    route = ChatsRoute,
    navOptions = navOptionsBuilder?.let { navOptions(it) }
)

fun NavGraphBuilder.chatsScreen(
    navigateToConversation: (chatId: String) -> Unit,
    navigateToTempConversation: (id: String) -> Unit,
    navigateToChatDetail: (chatId: String) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToArchivedChats: () -> Unit,
    navigateToChatInvitations: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    composable<ChatsRoute> {
        ChatsScreen(
            navigateToConversation = navigateToConversation,
            navigateToTempConversation = navigateToTempConversation,
            navigateToChatDetail = navigateToChatDetail,
            navigateToProfile = navigateToProfile,
            navigateToArchivedChats = navigateToArchivedChats,
            navigateToChatInvitations = navigateToChatInvitations,
            contentPadding = contentPadding,
            modifier = modifier
        )
    }
}