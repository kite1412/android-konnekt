package nrr.konnekt.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import nrr.konnekt.authentication.navigation.AuthenticationRoute
import nrr.konnekt.authentication.navigation.authenticationScreen

@Composable
internal fun KonnektNavHost(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues? = null,
    navController: NavHostController = rememberNavController(),
    isSignedIn: Boolean = true
) {
    NavHost(
        navController = navController,
        startDestination = AuthenticationRoute,
        modifier = modifier
    ) {
        authenticationScreen(
            contentPadding = rootContentPadding(scaffoldPadding),
            onSignedIn = {}
        )
    }
}