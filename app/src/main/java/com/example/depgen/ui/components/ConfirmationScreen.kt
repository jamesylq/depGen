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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
    title: String = "Are you sure?",
    body: String = "This action is irreversible!"
) {
    BasicAlertDialog(
        onDismissRequest = {
            onDecline()
        }
    ) {
        ElevatedCard (
            modifier = Modifier.height(400.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(90.dp))
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(80.dp))
                Text(body, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(80.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            onDecline()
                        },
                        modifier = Modifier.padding(end=60.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.Black,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onConfirm()
                        },
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
