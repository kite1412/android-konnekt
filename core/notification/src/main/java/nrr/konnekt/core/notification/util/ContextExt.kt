package nrr.konnekt.core.notification.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context

internal fun Context.notify(id: Int, notification: Notification) =
    notificationManager.notify(id, notification)

internal fun Context.cancelNotification(id: Int) =
    notificationManager.cancel(id)

internal val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager