package nrr.konnekt.feature.chats

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.domain.model.LatestChatMessage

@Composable
private fun ChatsScreen(
    chats: List<LatestChatMessage>,
    modifier: Modifier = Modifier
) {

}

@Preview
@Composable
private fun ChatsScreenPreview() {
    KonnektTheme {
        Scaffold {
            ChatsScreen(
                chats = emptyList(),
                modifier = Modifier.padding(it)
            )
        }
    }
}