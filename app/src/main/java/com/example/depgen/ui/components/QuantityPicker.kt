package com.example.depgen.ui.components

import android.util.Log
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun QuantityPicker(onUpdate: (Int) -> Unit, initialQty: Int = 0, minQty: Int = 0, maxQty: Int = Int.MAX_VALUE) {
    var qty by remember { mutableIntStateOf(initialQty) }
    var tf by remember { mutableStateOf("$initialQty") }

    Row (verticalAlignment = Alignment.CenterVertically) {
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
        TextField(
            value = tf,
            onValueChange = {
                tf = it
                try {
                    Log.wtf("DEBUG", tf)
                    if (tf.toInt() < 0) throw NumberFormatException()
                    onUpdate(tf.toInt())
                } catch (_: NumberFormatException) {}
            },
            modifier = Modifier
                .height(50.dp)
                .width(100.dp)
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
