package nrr.konnekt.core.model.util

import kotlinx.datetime.LocalDateTime

data class InstantInfo(
    val localDateTime: LocalDateTime,
    val isToday: Boolean,
    val daysAgo: Int
)