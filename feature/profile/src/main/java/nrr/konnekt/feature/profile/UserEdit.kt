package nrr.konnekt.feature.profile

data class UserEdit(
    val username: String,
    val profileImage: ByteArray?,
    val bio: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEdit

        if (username != other.username) return false
        if (!profileImage.contentEquals(other.profileImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + (profileImage?.contentHashCode() ?: 0)
        return result
    }
}
