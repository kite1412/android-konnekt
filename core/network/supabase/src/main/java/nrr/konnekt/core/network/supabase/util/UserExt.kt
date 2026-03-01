package nrr.konnekt.core.network.supabase.util

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.jsonPrimitive
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.supabase.dto.response.SupabaseUserActivityStatus

internal fun UserInfo.toUser() = User(
    id = this.id,
    email = this.email!!,
    username = this.userMetadata!!["username"]!!.jsonPrimitive.content,
    bio = null,
    imagePath = null,
    createdAt = this.createdAt!!
)

internal fun User.updateSupabaseUserActivityStatus() =
    SupabaseUserActivityStatus(
        userId = id,
        lastActiveAt = now()
    )