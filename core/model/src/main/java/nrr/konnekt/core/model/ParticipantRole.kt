package nrr.konnekt.core.model

enum class ParticipantRole {
    MEMBER,
    ADMIN;

    override fun toString(): String =
        this.name.lowercase()
}