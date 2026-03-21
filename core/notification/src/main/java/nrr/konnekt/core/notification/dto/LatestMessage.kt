package nrr.konnekt.core.notification.dto

import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nrr.konnekt.core.network.upload.util.CachingFileResolver
import nrr.konnekt.core.notification.util.MessageData
import nrr.konnekt.core.notification.util.getCircularBitmap
import kotlin.time.Instant

@Serializable
internal data class LatestMessage(
    val content: String,
    @SerialName("with_attachments")
    val withAttachments: Boolean,
    @SerialName("sent_at")
    val sentAt: Instant,
    @SerialName("sender_id")
    val senderId: String,
    @SerialName("sender_name")
    val senderName: String,
    @SerialName("sender_image_path")
    val senderImagePath: String?
) {
    suspend fun toMessageData(cache: CachingFileResolver) = MessageData(
        sender = Person.Builder()
            .apply {
                setName(senderName)
                setKey(senderId)
                senderImagePath?.let { imagePath ->
                    cache.getCircularBitmap(imagePath)?.let { bitmap ->
                        setIcon(IconCompat.createWithBitmap(bitmap))
                    }
                }
            }
            .build(),
        message = content,
        sentAt = sentAt
    )
}