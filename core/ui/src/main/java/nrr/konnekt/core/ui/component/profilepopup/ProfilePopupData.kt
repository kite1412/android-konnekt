package nrr.konnekt.core.ui.component.profilepopup

import nrr.konnekt.core.domain.util.name
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.User

data class ProfilePopupData(
    val name: String,
    val iconPath: String?,
    val description: String?
)

fun Chat.toChatPopupData() =
    ProfilePopupData(
        name = name(),
        iconPath = setting?.iconPath,
        description = setting?.description
    )

fun User.toChatPopupData() =
    ProfilePopupData(
        name = username,
        iconPath = imagePath,
        description = bio
    )