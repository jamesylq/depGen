package com.example.depgen.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.depgen.utils.clearFocusOnKeyboardDismiss
import com.example.depgen.utils.isInt


@Composable
fun QuantityPicker(onUpdate: (Int) -> Unit, initialQty: Int = 0, minQty: Int = 0, maxQty: Int = Int.MAX_VALUE, scale: Float = 1f) {
    var qty by remember { mutableIntStateOf(initialQty) }
    var tf by remember { mutableStateOf("$initialQty") }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.scale(scale)
    ) {
        IconButton(
            onClick = {
                onUpdate(--qty)
                tf = "$qty"
            },
            modifier = Modifier.background(
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(10.dp)
            ),
            enabled = (qty > minQty)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Back"
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        OutlinedTextField(
            value = tf,
            onValueChange = {
                tf = it
                if (tf.isInt()) {
                    qty = maxOf(minOf(tf.toInt(), maxQty), minQty)
                    onUpdate(qty)
                }
            },
            modifier = Modifier
                .height(50.dp)
                .width(100.dp)
                .clearFocusOnKeyboardDismiss(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.width(5.dp))
        IconButton(
            onClick = {
                onUpdate(++qty)
                tf = "$qty"
            },
            modifier = Modifier.background(
                MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(10.dp)
            ),
            enabled = (qty < maxQty)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Back"
            )
        }
    }
}
