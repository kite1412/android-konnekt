package nrr.konnekt.core.ui.util

import nrr.konnekt.core.model.util.currentLocalDateTime
import nrr.konnekt.core.model.util.toStringIgnoreSecond
import kotlin.time.Instant

internal fun Instant.toTimeString() =
    currentLocalDateTime().time.toStringIgnoreSecond()