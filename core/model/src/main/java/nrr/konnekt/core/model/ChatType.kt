package nrr.konnekt.core.model

enum class ChatType {
    PERSONAL,
    GROUP,
    CHAT_ROOM;

    override fun toString(): String =
        this.name.lowercase()
}