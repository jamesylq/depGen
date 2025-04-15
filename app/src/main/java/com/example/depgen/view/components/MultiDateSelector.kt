package com.example.depgen.view.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun MultiDateSelector(
    selectedDates: Set<LocalDate>,
    onDateToggle: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    highlightedDate: LocalDate? = null,
    selectedColor: Color = Color.LightGray,
    unselectedColor: Color = MaterialTheme.colorScheme.secondary
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    if (highlightedDate != null) Log.d("DEPGENDEBUG", highlightedDate.toString())
    else Log.d("DEPGENDEBUG", "null")

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
            }
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        + " " + currentMonth.year,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(
                onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeek.entries.forEach {
                Text(
                    text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val startOffset = (firstDayOfMonth.dayOfWeek.value % 7).let {
            if (it == 0) 6 else it - 1
        }

        val totalDays = startOffset + lastDayOfMonth.dayOfMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(totalDays) { index ->
                if (index < startOffset) {
                    Spacer(modifier = Modifier.size(0.dp))
                } else {
                    val date = currentMonth.atDay(index - startOffset + 1)
                    val isSelected = selectedDates.contains(date)
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isSelected) selectedColor else unselectedColor,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(5.dp))
                            .clickable { onDateToggle(date) }
                            .let {
                                if (date == highlightedDate) {
                                    it.border(3.dp, Color.Black, RoundedCornerShape(5.dp))
                                } else {
                                    it
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
