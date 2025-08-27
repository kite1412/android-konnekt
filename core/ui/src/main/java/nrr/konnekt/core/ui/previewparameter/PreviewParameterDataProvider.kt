package nrr.konnekt.core.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.util.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

data class PreviewParameterData(
    val latestChatMessages: List<LatestChatMessage>,
    val user: User
)

class PreviewParameterDataProvider
    : PreviewParameterProvider<PreviewParameterData> {

    override val values: Sequence<PreviewParameterData>
        get() = sequenceOf(
            PreviewParameterData(latestChatMessages, user)
        )

    val latestChatMessages = listOf(
        LatestChatMessage(
            chat = Chat(
                id = "c1",
                type = ChatType.PERSONAL,
                createdAt = Instant.parse("2025-07-01T10:00:00Z"),
                setting = ChatSetting(
                    name = "Alice & Bob",
                    iconPath = null,
                    description = null,
                    permissionSettings = null
                )
            ),
            message = Message(
                id = "m1",
                chatId = "c1",
                sender = User(
                    id = "u1",
                    email = "alice@example.com",
                    username = "Alice",
                    bio = "Loves coffee",
                    imagePath = "/images/alice.png",
                    createdAt = Instant.parse("2024-07-01T09:00:00Z")
                ),
                content = "Hey Bob, how’s your day?",
                sentAt = now(),
                editedAt = Instant.parse("2025-07-10T15:00:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    MessageStatus("m1", "u1", Instant.parse("2025-07-10T15:01:00Z"), false),
                    MessageStatus("m1", "u2", Instant.parse("2025-07-10T15:02:00Z"), false)
                ),
                attachments = listOf(
                    Attachment(
                        id = "a1",
                        type = AttachmentType.IMAGE,
                        path = "/attachments/meeting.png",
                        name = "meeting.png",
                        size = 2048,
                        metadata = null
                    )
                )
            )
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c2",
                type = ChatType.GROUP,
                createdAt = Instant.parse("2025-06-15T08:00:00Z"),
                setting = ChatSetting(
                    name = "Study Group",
                    iconPath = "/icons/study.png",
                    description = "CS101 discussions",
                    permissionSettings = ChatPermissionSettings(
                        editChatInfo = true,
                        sendMessages = true,
                        createEvents = true,
                        manageMembers = true
                    )
                )
            ),
            message = Message(
                id = "m2",
                chatId = "c2",
                sender = User(
                    id = "u2",
                    email = "bob@example.com",
                    username = "Bob",
                    bio = "Guitar player",
                    imagePath = "/images/bob.png",
                    createdAt = Instant.parse("2024-10-15T08:00:00Z")
                ),
                content = "Don’t forget our meeting at 7 PM!",
                sentAt = now() - 1.days,
                editedAt = now(),
                isHidden = false,
                messageStatuses = listOf(
                    MessageStatus("m2", "u1", Instant.parse("2025-07-05T12:05:00Z"), false),
                    MessageStatus("m2", "u2", Instant.parse("2025-07-05T12:00:10Z"), false),
                    MessageStatus("m2", "u3", null, false)
                )
            )
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c3",
                type = ChatType.CHAT_ROOM,
                createdAt = Instant.parse("2025-05-20T09:00:00Z"),
                setting = ChatSetting(
                    name = "Public Lounge",
                    iconPath = "/icons/lounge.png",
                    description = "Chill and chat",
                    permissionSettings = null
                )
            ),
            message = null
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c4",
                type = ChatType.PERSONAL,
                createdAt = Instant.parse("2025-07-20T10:00:00Z"),
                setting = ChatSetting("Charlie & Diana")
            ),
            message = Message(
                id = "m3",
                chatId = "c4",
                sender = User(
                    id = "u3",
                    email = "charlie@example.com",
                    username = "Charlie",
                    bio = null,
                    imagePath = null,
                    createdAt = Instant.parse("2025-01-10T09:00:00Z")
                ),
                content = "Are we still on for tonight?",
                sentAt = Instant.parse("2025-07-21T17:30:00Z"),
                editedAt = Instant.parse("2025-07-21T17:30:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    MessageStatus("m3", "u3", Instant.parse("2025-07-21T17:31:00Z"), false),
                    MessageStatus("m3", "u4", null, false)
                )
            )
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c5",
                type = ChatType.GROUP,
                createdAt = Instant.parse("2025-06-10T10:00:00Z"),
                setting = ChatSetting(
                    name = "Project Team",
                    iconPath = "/icons/project.png",
                    description = "Project X updates"
                )
            ),
            message = null
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c6",
                type = ChatType.CHAT_ROOM,
                createdAt = Instant.parse("2025-03-01T08:00:00Z"),
                setting = ChatSetting(
                    name = "Gaming Zone",
                    iconPath = "/icons/game.png"
                )
            ),
            message = null
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c7",
                type = ChatType.GROUP,
                createdAt = Instant.parse("2025-07-25T08:00:00Z"),
                setting = ChatSetting(
                    name = "Weekend Trip",
                    iconPath = "/icons/trip.png",
                    description = "Planning weekend getaway"
                )
            ),
            message = Message(
                id = "m4",
                chatId = "c7",
                sender = User(
                    id = "u4",
                    email = "diana@example.com",
                    username = "Diana",
                    bio = "Traveler ✈️",
                    imagePath = "/images/diana.png",
                    createdAt = Instant.parse("2025-05-15T08:00:00Z")
                ),
                content = "I’ve booked the cabin! 🏕️",
                sentAt = Instant.parse("2025-07-26T09:00:00Z"),
                editedAt = Instant.parse("2025-07-26T09:00:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    MessageStatus("m4", "u1", Instant.parse("2025-07-26T09:01:00Z"), false),
                    MessageStatus("m4", "u2", Instant.parse("2025-07-26T09:02:00Z"), false),
                    MessageStatus("m4", "u3", Instant.parse("2025-07-26T09:03:00Z"), false),
                    MessageStatus("m4", "u4", Instant.parse("2025-07-26T09:00:05Z"), false)
                )
            )
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c8",
                type = ChatType.PERSONAL,
                createdAt = Instant.parse("2025-07-30T08:00:00Z"),
                setting = ChatSetting("Bob & Diana")
            ),
            message = null
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c9",
                type = ChatType.CHAT_ROOM,
                createdAt = Instant.parse("2025-06-20T08:00:00Z"),
                setting = ChatSetting(
                    name = "Music Fans",
                    iconPath = "/icons/music.png"
                )
            ),
            message = null
        ),
        LatestChatMessage(
            chat = Chat(
                id = "c10",
                type = ChatType.GROUP,
                createdAt = Instant.parse("2025-05-25T08:00:00Z"),
                setting = ChatSetting(
                    name = "Hackathon Squad",
                    iconPath = "/icons/code.png",
                    description = "Build & Win!"
                )
            ),
            message = Message(
                id = "m5",
                chatId = "c10",
                sender = User(
                    id = "u1",
                    email = "alice@example.com",
                    username = "Alice",
                    bio = "Loves coffee",
                    imagePath = "/images/alice.png",
                    createdAt = Instant.parse("2024-07-01T09:00:00Z")
                ),
                content = "Push your code to GitHub before 10 PM!",
                sentAt = Instant.parse("2025-07-01T18:00:00Z"),
                editedAt = Instant.parse("2025-07-01T18:00:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    MessageStatus("m5", "u1", Instant.parse("2025-07-01T18:01:00Z"), false),
                    MessageStatus("m5", "u2", Instant.parse("2025-07-01T18:01:30Z"), false),
                    MessageStatus("m5", "u3", null, false)
                )
            )
        )
    )

    val user = User(
        id = "u1",
        email = "kite@example.com",
        username = "kite",
        bio = "Empty bio",
        createdAt = Instant.parse("2024-07-01T09:00:00Z")
    )
}