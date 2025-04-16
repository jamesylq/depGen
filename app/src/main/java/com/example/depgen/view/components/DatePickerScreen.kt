package com.example.depgen.view.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    initialDateMillis: Long
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                }
            ) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel", color = MaterialTheme.colorScheme.error)
            }
        }
//        colors = DatePickerDefaults.colors(
//            containerColor = MaterialTheme.colorScheme.tertiary,
//            titleContentColor = MaterialTheme.colorScheme.primary,
//            headlineContentColor = MaterialTheme.colorScheme.onBackground,
//            weekdayContentColor = MaterialTheme.colorScheme.primary,
//            selectedDayContainerColor = MaterialTheme.colorScheme.tertiary,
//            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
//            dayContentColor = MaterialTheme.colorScheme.onBackground
//        )
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
    }
}
