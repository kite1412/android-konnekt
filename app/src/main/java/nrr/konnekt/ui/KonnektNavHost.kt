package nrr.konnekt.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import nrr.konnekt.authentication.navigation.AuthenticationRoute
import nrr.konnekt.authentication.navigation.authenticationScreen
import nrr.konnekt.feature.chats.navigation.ChatsRoute
import nrr.konnekt.feature.chats.navigation.chatsScreen
import nrr.konnekt.feature.conversation.navigation.conversationScreen
import nrr.konnekt.feature.conversation.navigation.navigateToConversation
import nrr.konnekt.feature.conversation.navigation.navigateToTempPersonalConversation

@Composable
internal fun KonnektNavHost(
    isSignedIn: Boolean,
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues? = null,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = if (isSignedIn) ChatsRoute else AuthenticationRoute,
        modifier = modifier
    ) {
        authenticationScreen(
            contentPadding = rootContentPadding(scaffoldPadding)
        )
        chatsScreen(
            navigateToConversation = navController::navigateToConversation,
            navigateToTempConversation = navController::navigateToTempPersonalConversation,
            contentPadding = rootContentPadding(scaffoldPadding)
        )
        conversationScreen(
            navigateBack = { navController.popBackStack() },
            navigateToChatDetail = {},
            contentPadding = smallContentPadding(scaffoldPadding)
        )
    }
}