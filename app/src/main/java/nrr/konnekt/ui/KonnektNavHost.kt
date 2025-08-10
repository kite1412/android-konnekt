package nrr.konnekt.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import nrr.konnekt.authentication.navigation.AuthenticationRoute
import nrr.konnekt.authentication.navigation.authenticationScreen
import nrr.konnekt.feature.chats.navigation.chatsScreen
import nrr.konnekt.feature.chats.navigation.navigateToChats

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
            ) navController.navigateToChats()
    }
    NavHost(
        navController = navController,
        startDestination = AuthenticationRoute,
        modifier = modifier
    ) {
        chatsScreen(rootContentPadding(scaffoldPadding))
        authenticationScreen(
            contentPadding = rootContentPadding(scaffoldPadding),
            onSignedIn = {  }
        )
    }
}