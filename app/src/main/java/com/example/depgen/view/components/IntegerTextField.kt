package com.example.depgen.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun IntegerTextField(
    fieldName: String,
    onFieldEdit: (Int) -> Unit,
    onError: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    minEntry: Int = 0,
    maxEntry: Int = Int.MAX_VALUE,
    default: String = ""
) {
    var error by remember { mutableStateOf("") }
    var fieldValue by remember { mutableStateOf(default) }

    OutlinedTextField(
        value = fieldValue,
        onValueChange = {
            fieldValue = it
            if (fieldValue.toIntOrNull() != null) {
                val intVal = fieldValue.toInt()
                if (intVal < minEntry) {
                    error = "$fieldName must be at least $minEntry!"
                    onError(true)
                } else if (intVal > maxEntry) {
                    error = "$fieldName must be at most $maxEntry!"
                    onError(true)
                } else {
                    error = ""
                    onFieldEdit(intVal)
                    onError(false)
                }
            } else if (fieldValue.isNotEmpty()) {
                error = "$fieldName must be an integer!"
                onError(true)
            } else {
                error = "$fieldName cannot be empty!"
                onError(true)
            }
        },
        modifier = modifier,
        supportingText = {
            if (error != "") {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            if (error != "") {
                Icon(
                    Icons.Rounded.Warning,
                    "",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}