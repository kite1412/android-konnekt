package nrr.konnekt.core.ui.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun msToString(ms: Long): String {
    val seconds = ms / 1000
    val minute = seconds / 60
    val second = seconds % 60
    val hour = minute / 60

    return (if (hour > 0) "${hour}:" else "") + String.format("%02d:%02d", minute, second)
}