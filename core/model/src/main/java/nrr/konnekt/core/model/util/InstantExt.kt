package nrr.konnekt.core.model.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Instant

fun Instant.currentLocalDateTime() =
    toLocalDateTime(TimeZone.currentSystemDefault())

fun now() = Clock.System.now()

fun Instant.info() =
    with(currentLocalDateTime()) {
        val today = now().currentLocalDateTime()
        InstantInfo(
            localDateTime = this,
            isToday = date == today.date,
            daysAgo = date.daysUntil(today.date)
        )
    }

fun LocalTime.toStringIgnoreSecond() =
    "%02d:%02d".format(hour, minute)

fun LocalDateTime.toStringFormatted(): String =
    toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yy"))