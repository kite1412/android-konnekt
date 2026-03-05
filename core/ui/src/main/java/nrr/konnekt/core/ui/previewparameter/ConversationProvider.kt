package nrr.konnekt.core.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentMetadata
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatParticipantStatus
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.model.User
import nrr.konnekt.core.model.UserMessageStatus
import nrr.konnekt.core.model.util.now
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

data class Conversation(
    val chat: Chat,
    val messages: List<Message>
)

data class ConversationProvider(
    override val values: Sequence<Conversation> = sequenceOf(
        personalConversation,
        groupConversation,
        chatRoomConversation
    )
) : PreviewParameterProvider<Conversation>

private val now = now()

private val user1 = User("user1", "user1@email.com", "User One", createdAt = Instant.parse("2025-08-01T00:00:00Z"))
private val user2 = User("user2", "user2@email.com", "User Two", createdAt = Instant.parse("2025-08-01T00:00:00Z"))
private val user3 = User("user3", "user3@email.com", "User Three", createdAt = Instant.parse("2025-08-01T00:00:00Z"))
private val user4 = User("user4", "user4@email.com", "User Four", createdAt = Instant.parse("2025-08-01T00:00:00Z"))

private val personalConversation = Conversation(
    chat = Chat(
        id = "chat1",
        type = ChatType.PERSONAL,
        createdAt = Instant.parse("2025-08-18T00:00:00Z"),
        setting = ChatSetting(
            name = "Alice",
            iconPath = null,
            description = "Personal chat with Alice"
        ),
        participants = listOf(
            ChatParticipant(user1, ParticipantRole.MEMBER, ChatParticipantStatus(joinedAt = Instant.parse("2025-08-18T00:00:00Z"))),
            ChatParticipant(user2, ParticipantRole.MEMBER, ChatParticipantStatus(joinedAt = Instant.parse("2025-08-18T00:00:00Z")))
        )
    ),
    messages = listOf(
        Message(
            id = "m1",
            sender = user1,
            content = "Hey, how’s it going?",
            sentAt = now - 3.days,
            editedAt = now - 3.days,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user2, false)
            )
        ),
        Message(
            id = "m2",
            sender = user1,
            content = "Been a while since we last talked!",
            sentAt = now - 3.days + 5.minutes,
            editedAt = now - 3.days + 5.minutes,
            isHidden = true,
            messageStatuses = listOf(
                UserMessageStatus(user2, false)
            )
        ),
        Message(
            id = "m3",
            sender = user2,
            content = "Yeah, it’s been a long time!",
            sentAt = now - 1.days,
            editedAt = now - 1.days,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user1, false)
            )
        ),
        Message(
            id = "m4",
            sender = user2,
            content = "How are things going for you?",
            sentAt = now - 1.days + 3.minutes,
            editedAt = now - 1.days + 3.minutes,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user1,  false)
            )
        ),
        Message(
            id = "m5",
            sender = user1,
            content = "Things are going well, just been busy with work.",
            sentAt = now,
            editedAt = now,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user2, false)
            )
        ),
        Message(
            id = "m6",
            sender = user1,
            content = "Let’s catch up soon!",
            sentAt = now + 2.minutes,
            editedAt = now + 2.minutes,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user2, false)
            )
        ),
        Message(
            id = "m0",
            sender = user2,
            content = "Hey, remember that trip we planned?",
            sentAt = now - 5.days,
            editedAt = now - 5.days + 1.minutes,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user1, false)
            )
        ),
        Message(
            id = "m7",
            sender = user2,
            content = "I was just thinking about that the other day.",
            sentAt = now - 2.days,
            editedAt = now - 2.days,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user1, false)
            )
        ),
        Message(
            id = "m8",
            sender = user1,
            content = "Oh yeah! We should revisit the plan sometime soon.",
            sentAt = now - 2.days + 10.minutes,
            editedAt = now - 2.days + 12.minutes,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user2, false)
            )
        ),
        Message(
            id = "m9",
            sender = user2,
            content = "For sure, let’s do it after your work slows down.",
            sentAt = now - 12.hours,
            editedAt = now - 12.hours,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user1, false)
            )
        ),
        Message(
            id = "m10",
            sender = user1,
            content = "Sounds like a plan 😄",
            sentAt = now + 10.minutes,
            editedAt = now + 10.minutes,
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user2, false)
            )
        ),
        Message(
            id = "m11",
            sender = user2,
            content = "I’ll check my schedule later today.",
            sentAt = now + 15.minutes,
            editedAt = now + 15.minutes,
            isHidden = true,
            messageStatuses = listOf(
                UserMessageStatus(user1, false)
            )
        )
    )
)

private val groupConversation = Conversation(
    chat = Chat(
        id = "chat2",
        type = ChatType.GROUP,
        createdAt = Instant.parse("2025-08-17T12:00:00Z"),
        setting = ChatSetting(
            name = "Weekend Plans",
            iconPath = null,
            description = "Planning our weekend trip",
            permissionSettings = ChatPermissionSettings(
                editChatInfo = true,
                sendMessages = true,
                manageMembers = true
            )
        ),
        participants = listOf(
            ChatParticipant(user1, ParticipantRole.ADMIN, ChatParticipantStatus(joinedAt = Instant.parse("2025-08-17T12:00:00Z"))),
            ChatParticipant(user2, ParticipantRole.MEMBER, ChatParticipantStatus(joinedAt = Instant.parse("2025-08-17T12:05:00Z"))),
            ChatParticipant(user3, ParticipantRole.MEMBER, ChatParticipantStatus(joinedAt = Instant.parse("2025-08-17T12:10:00Z")))
        )
    ),
    messages = listOf(
        Message(
            id = "m3",
            sender = user3,
            content = "Where should we go this weekend?",
            sentAt = Instant.parse("2025-08-17T12:30:00Z"),
            editedAt = Instant.parse("2025-08-17T12:30:00Z"),
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user2, false),
                UserMessageStatus(user3, false)
            )
        ),
        Message(
            id = "m4",
            sender = user2,
            content = "How about the beach? 🏖",
            sentAt = Instant.parse("2025-08-17T12:32:00Z"),
            editedAt = Instant.parse("2025-08-17T12:32:00Z"),
            isHidden = false,
            attachments = listOf(
                Attachment(
                    "a1",
                    AttachmentType.IMAGE,
                    "beach.png",
                    "Beach",
                    1024,
                    AttachmentMetadata(1920, 1080, null, "image/png")
                )
            ),
            messageStatuses = listOf(
                UserMessageStatus(user1, false),
                UserMessageStatus(user3, false)
            )
        )
    )
)

private val chatRoomConversation = Conversation(
    chat = Chat(
        id = "chat3",
        type = ChatType.CHAT_ROOM,
        createdAt = Instant.parse("2025-08-10T10:00:00Z"),
        setting = ChatSetting(
            name = "Public Room",
            iconPath = null,
            description = "Open discussion for everyone"
        ),
        participants = listOf(
            ChatParticipant(user1, ParticipantRole.MEMBER, ChatParticipantStatus(joinedAt = Instant.parse("2025-08-10T10:00:00Z"))),
            ChatParticipant(
                user4,
                ParticipantRole.MEMBER,
                ChatParticipantStatus(joinedAt = Instant.parse("2025-08-11T14:00:00Z"))
            )
        )
    ),
    messages = listOf(
        Message(
            id = "m5",
            sender = user4,
            content = "Hello everyone 👋",
            sentAt = Instant.parse("2025-08-11T14:05:00Z"),
            editedAt = Instant.parse("2025-08-11T14:05:00Z"),
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user1, false),
            )
        ),
        Message(
            id = "m6",
            sender = user1,
            content = "Welcome to the room!",
            sentAt = Instant.parse("2025-08-11T14:06:00Z"),
            editedAt = Instant.parse("2025-08-11T14:06:00Z"),
            isHidden = false,
            messageStatuses = listOf(
                UserMessageStatus(user4, false)
            )
        )
    )
)