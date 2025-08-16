package nrr.konnekt.feature.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nrr.konnekt.core.designsystem.theme.KonnektTheme

@Composable
private fun ConversationScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

    }
}

@Preview
@Composable
private fun ConversationScreenPreview() {
    KonnektTheme {
        Scaffold {
            ConversationScreen(
                modifier = Modifier.padding(it)
            )
        }
    }
}