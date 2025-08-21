package nrr.konnekt.feature.conversation.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import nrr.konnekt.core.model.util.info
import nrr.konnekt.core.model.util.toStringFormatted

internal fun LocalDate.info() = atStartOfDayIn(TimeZone.UTC).info()

internal fun LocalDate.dateHeaderString() = with(info()) {
    if (isToday) "Today"
    else if (daysAgo == 1) "Yesterday"
    else toStringFormatted("dd MMMM yyyy")
}