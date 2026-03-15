package nrr.konnekt.core.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nrr.konnekt.core.domain.model.LatestChatMessage
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatInvitation
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserMessageStatus
import nrr.konnekt.core.model.util.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

data class PreviewParameterData(
    val latestChatMessages: List<LatestChatMessage>,
    val user: User,
    val chatInvitations: List<ChatInvitation>
)

class PreviewParameterDataProvider
    : PreviewParameterProvider<PreviewParameterData> {

    override val values: Sequence<PreviewParameterData>
        get() = sequenceOf(
            PreviewParameterData(latestChatMessages, user, chatInvitations)
        )

    val user1 = User(
        id = "u1",
        email = "alice@example.com",
        username = "Alice",
        bio = "Loves coffee",
        imagePath = "/images/alice.png",
        createdAt = Instant.parse("2024-07-01T09:00:00Z")
    )

    val user2 = User(
        id = "u2",
        email = "bob@example.com",
        username = "Bob",
        bio = "Guitar player",
        imagePath = "/images/bob.png",
        createdAt = Instant.parse("2024-10-15T08:00:00Z")
    )

    val user3 = User(
        id = "u3",
        email = "charlie@example.com",
        username = "Charlie",
        bio = null,
        imagePath = null,
        createdAt = Instant.parse("2025-01-10T09:00:00Z")
    )

    val user4 = User(
        id = "u4",
        email = "diana@example.com",
        username = "Diana",
        bio = "Traveler ✈️",
        imagePath = "/images/diana.png",
        createdAt = Instant.parse("2025-05-15T08:00:00Z")
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
                sender = user1,
                content = "",
                sentAt = now(),
                editedAt = Instant.parse("2025-07-10T15:00:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    UserMessageStatus(user1, false),
                    UserMessageStatus(user2, false)
                ),
                attachments = listOf(
                    Attachment(
                        id = "a1",
                        type = AttachmentType.VIDEO,
                        path = "/attachments/meeting.mp4",
                        name = "meeting.mp4",
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
                        manageMembers = true
                    )
                )
            ),
            message = Message(
                id = "m2",
                sender = user2,
                content = "Don’t forget our meeting at 7 PM!",
                sentAt = now() - 1.days,
                editedAt = now(),
                isHidden = false,
                messageStatuses = listOf(
                    UserMessageStatus(user1, false),
                    UserMessageStatus(user2, false),
                    UserMessageStatus(user3, false)
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
                sender = user3,
                content = "Are we still on for tonight?",
                sentAt = Instant.parse("2025-07-21T17:30:00Z"),
                editedAt = Instant.parse("2025-07-21T17:30:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    UserMessageStatus(user3, false),
                    UserMessageStatus(user4, false)
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
                sender = user4,
                content = "I’ve booked the cabin! 🏕️",
                sentAt = Instant.parse("2025-07-26T09:00:00Z"),
                editedAt = Instant.parse("2025-07-26T09:00:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    UserMessageStatus(user1, false),
                    UserMessageStatus(user2, false),
                    UserMessageStatus(user3, false),
                    UserMessageStatus(user4, false)
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
                sender = user1,
                content = "Push your code to GitHub before 10 PM!",
                sentAt = Instant.parse("2025-07-01T18:00:00Z"),
                editedAt = Instant.parse("2025-07-01T18:00:00Z"),
                isHidden = false,
                messageStatuses = listOf(
                    UserMessageStatus(user1, false),
                    UserMessageStatus(user2, false),
                    UserMessageStatus(user3, false)
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
    
    val chatInvitations = listOf(
        ChatInvitation(
            id = "i1",
            chat = Chat(
                id = "c11",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Android Devs, With Much Much Much Much Much Longer Name")
            ),
            inviter = user1,
            receiver = user,
            invitedAt = now() - 2.days
        ),
        ChatInvitation(
            id = "i2",
            chat = Chat(
                id = "c12",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Kotlin Enthusiasts")
            ),
            inviter = user2,
            receiver = user,
            invitedAt = now() - 1.days
        ),
        ChatInvitation(
            id = "i3",
            chat = Chat(
                id = "c13",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Designers")
            ),
            inviter = user3,
            receiver = user,
            invitedAt = now() - 5.hours
        ),
        ChatInvitation(
            id = "i4",
            chat = Chat(
                id = "c14",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Gamers")
            ),
            inviter = user4,
            receiver = user,
            invitedAt = now() - 10.minutes
        ),
        ChatInvitation(
            id = "i5",
            chat = Chat(
                id = "c15",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Movie Buffs")
            ),
            inviter = user2,
            receiver = user,
            invitedAt = now() - 3.days
        ),
        ChatInvitation(
            id = "i6",
            chat = Chat(
                id = "c16",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Foodies")
            ),
            inviter = user1,
            receiver = user,
            invitedAt = now() - 4.days
        ),
        ChatInvitation(
            id = "i7",
            chat = Chat(
                id = "c17",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Travelers")
            ),
            inviter = user4,
            receiver = user,
            invitedAt = now() - 6.days
        ),
        ChatInvitation(
            id = "i8",
            chat = Chat(
                id = "c18",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Tech News")
            ),
            inviter = user3,
            receiver = user,
            invitedAt = now() - 7.days
        ),
        ChatInvitation(
            id = "i9",
            chat = Chat(
                id = "c19",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Book Club")
            ),
            inviter = user2,
            receiver = user,
            invitedAt = now() - 8.days
        ),
        ChatInvitation(
            id = "i10",
            chat = Chat(
                id = "c20",
                type = ChatType.GROUP,
                createdAt = now(),
                setting = ChatSetting("Fitness Junkies")
            ),
            inviter = user1,
            receiver = user,
            invitedAt = now() - 9.days
        )
    )
}
