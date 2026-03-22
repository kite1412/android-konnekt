package nrr.konnekt.core.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.Authentication
import nrr.konnekt.core.model.User
import nrr.konnekt.core.network.upload.util.CachingFileResolver
import nrr.konnekt.core.notification.util.ChatNotificationData
import nrr.konnekt.core.notification.util.KonnektNotification
import nrr.konnekt.core.notification.util.getCircularBitmap
import nrr.konnekt.core.notification.util.notify
import javax.inject.Inject
import javax.inject.Singleton

private const val LOG_TAG = "NotificationManager"

@Singleton
internal class NotificationManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authentication: Authentication,
    private val cache: CachingFileResolver
) {
    fun notifyNewMessages(
        data: ChatNotificationData
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            getCurrentPerson()?.let { person ->
                context.notify(
                    /*id = */data.id.hashCode(),
                    /*notification = */KonnektNotification.Messages.createNotification(
                        context = context,
                        currentPerson = person,
                        chat = data
                    )
                )
                Log.d(LOG_TAG, "notified: $data")
            }
        }
    }

    fun isNotificationPermissionGranted(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.checkSelfPermission(
                /*context = */context,
                /*permission = */Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        else true

    private suspend fun getCurrentPerson(): Person? =
        authentication
            .loggedInUser
            .first { it != null }
            ?.toPerson()

    private suspend fun User.toPerson() = Person.Builder()
        .apply {
            setName(username)
            setKey(id)
            cache.getCircularBitmap(imagePath)?.let { bitmap ->
                setIcon(IconCompat.createWithBitmap(bitmap))
            }
        }
        .build()
}