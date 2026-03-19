package nrr.konnekt.core.notification.util

import android.content.Context
import android.content.Intent

internal fun safeOnReceive(
    context: Context?,
    intent: Intent?,
    block: (Context, Intent) -> Unit
) {
    if (context != null && intent != null) block(context, intent)
}