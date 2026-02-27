package nrr.konnekt.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.feature.profile.ProfileScreen

@Serializable
data object ProfileRoute

fun NavController.navigateToProfile() = navigate(ProfileRoute)

fun NavGraphBuilder.profileScreen(
    navigateBack: () -> Unit
) {
    composable<ProfileRoute> {
        ProfileScreen(
            navigateBack = navigateBack
        )
    }
}