package nrr.konnekt.authentication.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.authentication.AuthenticationScreen

@Serializable
object AuthenticationRoute

fun NavGraphBuilder.authenticationScreen(
    contentPadding: PaddingValues,
    onSignedIn: () -> Unit
) {
    composable<AuthenticationRoute> {
        AuthenticationScreen(
            contentPadding = contentPadding,
            onSignedIn = onSignedIn
        )
    }
}