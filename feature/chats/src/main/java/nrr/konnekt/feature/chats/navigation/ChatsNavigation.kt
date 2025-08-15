package nrr.konnekt.feature.chats.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.core.model.Chat
import nrr.konnekt.feature.chats.ChatsScreen

@Serializable
object ChatsRoute

fun NavController.navigateToChats(navOptions: NavOptions? = null) =
    navigate(ChatsRoute, navOptions)

fun NavGraphBuilder.chatsScreen(
    navigateToCreateGroupChat: () -> Unit,
    navigateToConversation: (Chat) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    composable<ChatsRoute> {
        ChatsScreen(
            navigateToCreateGroupChat = navigateToCreateGroupChat,
            navigateToConversation = navigateToConversation,
            contentPadding = contentPadding,
            modifier = modifier
        )
    }
}