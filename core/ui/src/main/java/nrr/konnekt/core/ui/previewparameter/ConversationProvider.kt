package nrr.konnekt.core.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import nrr.konnekt.core.model.Attachment
import nrr.konnekt.core.model.AttachmentMetadata
import nrr.konnekt.core.model.AttachmentType
import nrr.konnekt.core.model.Chat
import nrr.konnekt.core.model.ChatParticipant
import nrr.konnekt.core.model.ChatPermissionSettings
import nrr.konnekt.core.model.ChatSetting
import nrr.konnekt.core.model.ChatType
import nrr.konnekt.core.model.Message
import nrr.konnekt.core.model.MessageStatus
import nrr.konnekt.core.model.ParticipantRole
import nrr.konnekt.core.model.util.now
import kotlin.time.Duration.Companion.days
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
            ChatParticipant("chat1", "user1", ParticipantRole.MEMBER, Instant.parse("2025-08-18T00:00:00Z"), null),
            ChatParticipant("chat1", "user2", ParticipantRole.MEMBER, Instant.parse("2025-08-18T00:00:00Z"), null)
        )
    ),
    messages = listOf(
        Message(
            id = "m1",
            chatId = "chat1",
            senderId = "user1",
            content = "Hey, how’s it going?",
            sentAt = now - 3.days,
            editedAt = now - 3.days,
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m1", "user2", now - 3.days + 2.minutes, false)
            )
        ),
        Message(
            id = "m2",
            chatId = "chat1",
            senderId = "user1",
            content = "Been a while since we last talked!",
            sentAt = now - 3.days + 5.minutes,
            editedAt = now - 3.days + 5.minutes,
            isHidden = true,
            messageStatuses = listOf(
                MessageStatus("m2", "user2", now - 3.days + 7.minutes, false)
            )
        ),
        Message(
            id = "m3",
            chatId = "chat1",
            senderId = "user2",
            content = "Yeah, it’s been a long time!",
            sentAt = now - 1.days,
            editedAt = now - 1.days,
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m3", "user1", now - 1.days + 1.minutes, false)
            )
        ),
        Message(
            id = "m4",
            chatId = "chat1",
            senderId = "user2",
            content = "How are things going for you?",
            sentAt = now - 1.days + 3.minutes,
            editedAt = now - 1.days + 3.minutes,
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m4", "user1", now - 1.days + 5.minutes, false)
            )
        ),
        Message(
            id = "m5",
            chatId = "chat1",
            senderId = "user1",
            content = "Things are going well, just been busy with work.",
            sentAt = now,
            editedAt = now,
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m5", "user2", now + 1.minutes, false)
            )
        ),
        Message(
            id = "m6",
            chatId = "chat1",
            senderId = "user1",
            content = "Let’s catch up soon!",
            sentAt = now + 2.minutes,
            editedAt = now + 2.minutes,
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m6", "user2", now + 3.minutes, false)
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
                createEvents = true,
                manageMembers = true
            )
        ),
        participants = listOf(
            ChatParticipant("chat2", "user1", ParticipantRole.ADMIN, Instant.parse("2025-08-17T12:00:00Z"), null),
            ChatParticipant("chat2", "user2", ParticipantRole.MEMBER, Instant.parse("2025-08-17T12:05:00Z"), null),
            ChatParticipant("chat2", "user3", ParticipantRole.MEMBER, Instant.parse("2025-08-17T12:10:00Z"), null)
        )
    ),
    messages = listOf(
        Message(
            id = "m3",
            chatId = "chat2",
            senderId = "user1",
            content = "Where should we go this weekend?",
            sentAt = Instant.parse("2025-08-17T12:30:00Z"),
            editedAt = Instant.parse("2025-08-17T12:30:00Z"),
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m3", "user2", Instant.parse("2025-08-17T12:31:00Z"), false),
                MessageStatus("m3", "user3", Instant.parse("2025-08-17T12:31:30Z"), false)
            )
        ),
        Message(
            id = "m4",
            chatId = "chat2",
            senderId = "user2",
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
                MessageStatus("m4", "user1", Instant.parse("2025-08-17T12:33:00Z"), false),
                MessageStatus("m4", "user3", Instant.parse("2025-08-17T12:34:00Z"), false)
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
            ChatParticipant("chat3", "user1", ParticipantRole.MEMBER, Instant.parse("2025-08-10T10:00:00Z"), null),
            ChatParticipant(
                "chat3",
                "user4",
                ParticipantRole.MEMBER,
                Instant.parse("2025-08-11T14:00:00Z"),
                null
            )
        )
    ),
    messages = listOf(
        Message(
            id = "m5",
            chatId = "chat3",
            senderId = "user4",
            content = "Hello everyone 👋",
            sentAt = Instant.parse("2025-08-11T14:05:00Z"),
            editedAt = Instant.parse("2025-08-11T14:05:00Z"),
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m5", "user1", Instant.parse("2025-08-11T14:06:30Z"), false),
            )
        ),
        Message(
            id = "m6",
            chatId = "chat3",
            senderId = "user1",
            content = "Welcome to the room!",
            sentAt = Instant.parse("2025-08-11T14:06:00Z"),
            editedAt = Instant.parse("2025-08-11T14:06:00Z"),
            isHidden = false,
            messageStatuses = listOf(
                MessageStatus("m6", "user4", Instant.parse("2025-08-11T14:07:00Z"), false)
            )
        )
    )
)