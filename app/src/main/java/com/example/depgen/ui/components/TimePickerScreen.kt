package com.example.depgen.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerScreen(setPicking: (Int) -> Unit, setText: (String) -> Unit, title: String) {
    val calendar = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    BasicAlertDialog(
        onDismissRequest = {
            setPicking(0)
        }
    ) {
        ElevatedCard (
            colors = CardDefaults.cardColors(
                containerColor = Color(220, 200, 200),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 30.dp)
                )
                TimePicker(state = timePickerState)
                Spacer(modifier = Modifier.weight(1f))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val hr = timePickerState.hour.toString().padStart(2, '0')
                            val min = timePickerState.minute.toString().padStart(2, '0')
                            setText("$hr:$min")
                            setPicking(0)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
