package nrr.konnekt.core.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import nrr.konnekt.core.domain.Authentication
import javax.inject.Inject

class NotificationManager @Inject constructor(
    authentication: Authentication
) {
    fun isNotificationPermissionGranted(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.checkSelfPermission(
                /*context = */context,
                /*permission = */Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        else true
}