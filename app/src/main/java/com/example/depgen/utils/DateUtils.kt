package com.example.depgen.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.util.fastJoinToString
import com.example.depgen.MAX_REPEATING_DEPLOYMENT_DAYS
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

val DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

const val NO_DATE = "0001-01-01T00:00:00"

@RequiresApi(Build.VERSION_CODES.O)
val NO_DATE_OBJ: LocalDateTime = LocalDateTime.parse(NO_DATE)

@RequiresApi(Build.VERSION_CODES.O)
val DATETIMEFORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@RequiresApi(Build.VERSION_CODES.O)
val NATURAL_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

@RequiresApi(Build.VERSION_CODES.O)
fun getDays(start: LocalDate, end: LocalDate, daysOfWeek: List<Boolean>): ArrayList<LocalDate> {
    if (start.isAfter(end)) {
        throw IllegalArgumentException("Start Date is after End Date!")
    } else if (ChronoUnit.DAYS.between(start, end) > MAX_REPEATING_DEPLOYMENT_DAYS) {
        throw IllegalArgumentException("Start Date and End Date Too Far Apart!")
    }

    val days = ArrayList<LocalDate>()
    var cur = start
    while (!cur.isAfter(end)) {
        if (daysOfWeek[cur.dayOfWeek.value - 1]) days.add(cur)
        cur = cur.plusDays(1)
    }

    return days
}

fun daysOfWeekToString(daysOfWeek: List<Boolean>): String {
    val selected = ArrayList<String>()
    for (i in 0..6) {
        if (daysOfWeek[i]) {
            selected.add(DAYS[i])
        }
    }

    return when (
        daysOfWeek.joinToString(separator = "") {
            if (it) "1" else "0"
        }
    ) {
        "1111100" -> "every weekday"
        "0000011" -> "every weekend"
        else -> when (selected.size) {
            7 -> "everyday"
            1 -> "every ${selected[0]}"
            else -> "every " + selected.subList(0, selected.size - 1).fastJoinToString(", ") +
            " and " + selected[selected.size - 1]
        }
    }
}
