package nrr.konnekt.feature.conversation.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import nrr.konnekt.core.model.util.info

internal fun LocalDate.info() = atStartOfDayIn(TimeZone.UTC).info()