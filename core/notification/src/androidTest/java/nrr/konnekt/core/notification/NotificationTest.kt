package nrr.konnekt.core.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.notification.util.ChatNotificationData
import nrr.konnekt.core.notification.util.KonnektNotification
import nrr.konnekt.core.notification.util.MessageData
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class NotificationTest {
    @Test
    fun sendMessagesNotificationSuccess() {
        with(ApplicationProvider.getApplicationContext<Context>()) {
            val notification = KonnektNotification.Messages.createNotification(
                context = this,
                currentPerson = Person.Builder()
                    .setName("Kite")
                    .build(),
                chat = ChatNotificationData(
                    id = "any:will:do",
                    name = "A Group",
                    icon = null,
                    messages = listOf(
                        MessageData(
                            sender = Person.Builder()
                                .setName("Other User")
                                .setIcon(IconCompat.createWithResource(this, R.drawable.logo_small))
                                .build(),
                            message = "Test",
                            sentAt = now()
                        ),
                        MessageData(
                            sender = Person.Builder()
                                .setName("Other User")
                                .setIcon(IconCompat.createWithResource(this, R.drawable.logo_small))
                                .build(),
                            message = "Test 2",
                            sentAt = now()
                        ),
                        MessageData(
                            sender = Person.Builder()
                                .setName("Other User 2")
                                .setIcon(IconCompat.createWithResource(this, R.drawable.logo_small))
                                .build(),
                            message = "Another test",
                            sentAt = now()
                        )
                    )
                )
            )

            NotificationManagerCompat.from(this)
                .notify(1, notification)
        }
    }
}