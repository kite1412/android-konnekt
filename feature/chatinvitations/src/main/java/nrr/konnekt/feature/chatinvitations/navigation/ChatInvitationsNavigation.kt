package nrr.konnekt.feature.chatinvitations.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.feature.chatinvitations.ChatInvitationsScreen

@Serializable
data object ChatInvitationsRoute

fun NavController.navigateToChatInvitations() =
    navigate(ChatInvitationsRoute)

fun NavGraphBuilder.chatInvitationsScreen(
    navigateBack: () -> Unit,
    contentPadding: PaddingValues
) {
    composable<ChatInvitationsRoute> {
        ChatInvitationsScreen(
            navigateBack = navigateBack,
            contentPadding = contentPadding
        )
    }
}