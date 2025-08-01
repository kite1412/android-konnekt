package nrr.konnekt.core.network.supabase.util

import io.github.jan.supabase.auth.user.UserInfo
import nrr.konnekt.core.model.User

internal fun UserInfo.toUser() = User(
    id = this.id,
    email = this.email!!,
    username = this.userMetadata!!["username"]!!.toString(),
    bio = null,
    createdAt = this.createdAt!!
)