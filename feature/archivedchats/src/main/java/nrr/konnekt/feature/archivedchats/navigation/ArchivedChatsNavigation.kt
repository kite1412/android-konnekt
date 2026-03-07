package nrr.konnekt.feature.archivedchats.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.feature.archivedchats.ArchivedChatsScreen

@Serializable
data object ArchivedChatsRoute

fun NavController.navigateToArchivedChats() =
    navigate(ArchivedChatsRoute)

fun NavGraphBuilder.archivedChatsScreen(
    navigateBack: () -> Unit,
    navigateToConversation: (id: String) -> Unit,
    contentPadding: PaddingValues
) {
    composable<ArchivedChatsRoute> {
        ArchivedChatsScreen(
            navigateBack = navigateBack,
            navigateToConversation = navigateToConversation,
            contentPadding = contentPadding
        )
    }
}