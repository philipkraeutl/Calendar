package io.wojciechosak.calendar.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month

fun isLeapYear(year: Int): Boolean {
    return try {
        LocalDate(year, 2, 29)
        true
    } catch (exception: IllegalArgumentException) {
        false
    }
}

fun monthLength(month: Month, year: Int): Int {
    val isLeapYear = isLeapYear(year)
    return when (month) {
        Month.FEBRUARY -> if (isLeapYear) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
}

internal fun LocalDateTime.toLocalDate(): LocalDate {
    return LocalDate(year, month, dayOfMonth)
}

internal fun LocalDate.copy(
    year: Int = this.year,
    month: Month = this.month,
    day: Int = this.dayOfMonth
): LocalDate {
    return LocalDate(year, month, day)
}