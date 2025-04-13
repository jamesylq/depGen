package com.example.depgen.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.view.fragments.removeLetters
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateInputRow(
    title: String,
    day: String, month: String, year: String,
    updateDay: (String) -> Unit, updateMonth: (String) -> Unit, updateYear: (String) -> Unit,
    defaultDate: LocalDate? = null
) {
    var pickingDate by remember { mutableStateOf(false) }

    if (pickingDate) {
        var currentSelection = (defaultDate ?: LocalDate.now().plusDays(1))
            .atTime(12, 0)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        try { currentSelection = LocalDate.of(year.toInt(), month.toInt(), day.toInt()) }
        catch (_: DateTimeParseException) { }
        catch (_: NumberFormatException) { }

        DatePickerScreen (
            onDateSelected = {
                updateDay(it.dayOfMonth.toString().padStart(2, '0'))
                updateMonth(it.monthValue.toString().padStart(2, '0'))
                updateYear(it.year.toString().padStart(4, '0'))
            },
            onDismiss = {
                pickingDate = false
            },
            initialDateMillis = currentSelection
                .atTime(12, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()!!
                .toEpochMilli()
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title: ",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(end = 8.dp).weight(0.22f)
        )
        TextField(
            value = day,
            onValueChange = {
                updateDay(removeLetters(it))
                if (day.length > 2) updateDay(day.substring(0, 2))

                if (month.isNotBlank()) updateMonth(month.padStart(2, '0'))
                if (year.isNotBlank()) updateYear(year.padStart(4, '0'))
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(0.22f)
                .clearFocusOnKeyboardDismiss(),
            placeholder = {
                Text("DD")
            }
        )
        TextField(
            value = month,
            onValueChange = {
                updateMonth(removeLetters(it))
                if (month.length > 2) updateMonth(month.substring(0, 2))

                if (day.isNotBlank()) updateDay(day.padStart(2, '0'))
                if (year.isNotBlank()) updateYear(year.padStart(4, '0'))
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(0.22f)
                .clearFocusOnKeyboardDismiss(),
            placeholder = {
                Text("MM")
            }
        )
        TextField(
            value = year,
            onValueChange = {
                updateYear(removeLetters(it))
                if (year.length > 4) updateYear(year.substring(0, 4))

                if (day.isNotBlank()) updateDay(day.padStart(2, '0'))
                if (month.isNotBlank()) updateMonth(month.padStart(2, '0'))
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(0.34f)
                .clearFocusOnKeyboardDismiss(),
            placeholder = {
                Text("YYYY")
            }
        )
        IconButton(
            onClick = {
                pickingDate = true
            },
            modifier = Modifier.width(50.dp)
        ) {
            Icon(Icons.Default.DateRange, "")
        }
    }
}