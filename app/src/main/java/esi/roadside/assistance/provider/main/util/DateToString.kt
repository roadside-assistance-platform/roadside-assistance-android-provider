package esi.roadside.assistance.provider.main.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun ZonedDateTime.toStringDate(): String {
    return this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}