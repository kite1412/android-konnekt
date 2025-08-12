package nrr.konnekt.feature.chats.util

internal enum class ChatFilter(displayName: String) {
    ALL("All"),
    PERSON("Person"),
    GROUP("Group"),
    CHAT_ROOM("Chat Room");

    override fun toString(): String {
        return this.name
            .lowercase()
            .split("_")
            .joinToString(" ") {
                it.replaceFirstChar { c -> c.uppercase() }
            }
    }
}