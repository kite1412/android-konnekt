package nrr.konnekt.feature.conversation.util

import androidx.compose.ui.graphics.Color
import nrr.konnekt.core.designsystem.theme.BrightBlue
import nrr.konnekt.core.designsystem.theme.BrightRed
import nrr.konnekt.core.designsystem.theme.Magenta
import nrr.konnekt.core.designsystem.theme.Yellow
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.model.AttachmentType

internal data class Attachment(
    val type: AttachmentType,
    val iconId: Int,
    val iconTint: Color
)

internal val attachments = listOf(
    Attachment(
        type = AttachmentType.IMAGE,
        iconId = KonnektIcon.image,
        iconTint = Yellow
    ),
    Attachment(
        type = AttachmentType.VIDEO,
        iconId = KonnektIcon.video,
        iconTint = BrightRed
    ),
    Attachment(
        type = AttachmentType.DOCUMENT,
        iconId = KonnektIcon.file,
        iconTint = BrightBlue
    ),
    Attachment(
        type = AttachmentType.AUDIO,
        iconId = KonnektIcon.audio,
        iconTint = Magenta
    )
)