package nrr.konnekt.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import nrr.konnekt.feature.archivedchats.navigation.archivedChatsScreen
import nrr.konnekt.feature.archivedchats.navigation.navigateToArchivedChats
import nrr.konnekt.feature.authentication.navigation.AuthenticationRoute
import nrr.konnekt.feature.authentication.navigation.authenticationScreen
import nrr.konnekt.feature.chatdetail.navigation.chatDetailScreen
import nrr.konnekt.feature.chatdetail.navigation.navigateToChatDetail
import nrr.konnekt.feature.chatdetail.navigation.navigateToTempPersonalChatDetail
import nrr.konnekt.feature.chats.navigation.ChatsRoute
import nrr.konnekt.feature.chats.navigation.chatsScreen
import nrr.konnekt.feature.conversation.navigation.ConversationRoute
import nrr.konnekt.feature.conversation.navigation.conversationScreen
import nrr.konnekt.feature.conversation.navigation.navigateToConversation
import nrr.konnekt.feature.conversation.navigation.navigateToTempPersonalConversation
import nrr.konnekt.feature.conversation.util.IdType
import nrr.konnekt.feature.profile.navigation.navigateToProfile
import nrr.konnekt.feature.profile.navigation.profileScreen

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
            navigateToChatDetail = navController::navigateToChatDetail,
            navigateToProfile = navController::navigateToProfile,
            navigateToArchivedChats = navController::navigateToArchivedChats,
            contentPadding = rootContentPadding(scaffoldPadding)
        )
        conversationScreen(
            navController = navController,
            navigateBack = navController::popBackStack,
            navigateToChatDetail = { id, type ->
                when (type) {
                    IdType.CHAT -> navController::navigateToChatDetail
                    IdType.USER -> navController::navigateToTempPersonalChatDetail
                }(id)
            },
            contentPadding = smallContentPadding(scaffoldPadding)
        )
        chatDetailScreen(
            navController = navController,
            navigateBack = navController::popBackStack,
            navigateToConversation = { isChatId, id ->
                if (isChatId) {
                    navController.popBackStack(
                        route = ConversationRoute::class,
                        inclusive = true
                    )
                    navController.navigateToConversation(id)
                }
                else navController.navigateToTempPersonalConversation(id)
            },
            contentPadding = contentPadding(scaffoldPadding)
        )
        profileScreen(
            navigateBack = navController::popBackStack,
            contentPadding = contentPadding(scaffoldPadding)
        )
        archivedChatsScreen(
            navigateBack = navController::popBackStack,
            navigateToConversation = navController::navigateToConversation,
            navigateToChatDetail = navController::navigateToChatDetail,
            contentPadding = contentPadding(scaffoldPadding)
        )
    }
}