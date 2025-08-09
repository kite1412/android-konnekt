package nrr.konnekt.core.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nrr.konnekt.core.domain.model.LatestChatMessage

class LatestChatMessagesPreviewParameterProvider
    : PreviewParameterProvider<List<LatestChatMessage>> {
    override val values: Sequence<List<LatestChatMessage>>
        get() = sequenceOf(PreviewParameterData.latestChatMessages)
}