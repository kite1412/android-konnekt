package nrr.konnekt.core.notification.util

import android.app.NotificationManager
import android.content.Context

internal fun Context.cancelNotification(id: Int) {
    notificationManager.cancel(id)
}

internal val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager