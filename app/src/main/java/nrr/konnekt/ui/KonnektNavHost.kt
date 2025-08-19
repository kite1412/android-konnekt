package nrr.konnekt.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import nrr.konnekt.authentication.navigation.AuthenticationRoute
import nrr.konnekt.authentication.navigation.authenticationScreen
import nrr.konnekt.feature.chats.navigation.chatsScreen
import nrr.konnekt.feature.chats.navigation.navigateToChats
import nrr.konnekt.feature.conversation.navigation.conversationScreen
import nrr.konnekt.feature.conversation.navigation.navigateToConversation

@Composable
internal fun KonnektNavHost(
    isSignedIn: Boolean,
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues? = null,
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(isSignedIn) {
        if (isSignedIn)
            if (
                navController
                    .currentBackStackEntry
                    ?.destination
                    ?.hasRoute<AuthenticationRoute>() == true
            ) navController.navigateToChats(
                navOptions = navOptions {
                    popUpTo(AuthenticationRoute) {
                        inclusive = true
                    }
                }
            )
    }
    NavHost(
        navController = navController,
        startDestination = AuthenticationRoute,
        modifier = modifier
    ) {
        authenticationScreen(
            contentPadding = rootContentPadding(scaffoldPadding),
            onSignedIn = {  }
        )
        chatsScreen(
            navigateToCreateGroupChat = {},
            navigateToConversation = {
                navController.navigateToConversation(it.id)
            },
            contentPadding = rootContentPadding(scaffoldPadding)
        )
        conversationScreen(
            navigateBack = { navController.popBackStack() },
            navigateToChatDetail = {},
            contentPadding = contentPadding(scaffoldPadding)
        )
    }
}