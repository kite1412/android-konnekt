package nrr.konnekt.feature.chats.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import nrr.konnekt.feature.chats.ChatsScreen

@Serializable
object ChatsRoute

fun NavGraphBuilder.chatsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    composable<ChatsRoute> {
        ChatsScreen(
            contentPadding = contentPadding,
            modifier = modifier
        )
    }
}