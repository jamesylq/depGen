package com.example.depgen.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import com.example.depgen.utils.clearFocusOnKeyboardDismiss

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String = "Select",
    errorMessage: String = "",
    resetErrorMessage: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf(TextFieldValue(selectedOption)) }
    val focusManager = LocalFocusManager.current

    val filteredOptions = options.filter {
        it.contains(inputText.text, ignoreCase = true)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = inputText,
            onValueChange = {
                inputText = it
                expanded = true
                resetErrorMessage()
                onOptionSelected(it.text)
            },
            label = { Text(label) },
            trailingIcon = {
                if (errorMessage != "") {
                    Icon(
                        Icons.Rounded.Warning,
                        "",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            },
            supportingText = {
                if (errorMessage != "") {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .clearFocusOnKeyboardDismiss()
        )

        ExposedDropdownMenu(
            expanded = expanded && filteredOptions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filteredOptions.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        inputText = TextFieldValue(selectionOption)
                        onOptionSelected(selectionOption)
                        expanded = false
                        focusManager.clearFocus()
                        resetErrorMessage()
                    }
                )
            }
        }
    }
}
