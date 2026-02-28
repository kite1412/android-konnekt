package nrr.konnekt.core.domain.model

import nrr.konnekt.core.domain.dto.FileUpload

data class UserEdit(
    val username: String,
    val profileImage: FileUpload?,
    val bio: String?
)