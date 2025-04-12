package com.example.depgen.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RepeatDaySelector(
    onUpdate: (Int) -> Unit
) {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
    val selectedDays = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(7) {
                add(false)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            daysOfWeek.forEachIndexed { index, day ->
                val isSelected = selectedDays[index]
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.secondary
                            else Color.LightGray
                        )
                        .clickable {
                            selectedDays[index] = !selectedDays[index]
                            onUpdate(index)
                        }
                ) {
                    Text(
                        text = day,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
