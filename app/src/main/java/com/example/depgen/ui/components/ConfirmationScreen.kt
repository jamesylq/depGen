package com.example.depgen.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
    title: String = "Are you sure?",
    body: String = "Any unsaved edits will be erased!"
) {
    BasicAlertDialog(
        onDismissRequest = {
            onDecline()
        }
    ) {
        ElevatedCard (
            modifier = Modifier.height(520.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Image(
                    painterResource(R.drawable.icon_512),
                    "",
                    modifier = Modifier.clip(RoundedCornerShape(30.dp))
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(60.dp))
                Text(body, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(60.dp))
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
                            disabledContainerColor = MaterialTheme.colorScheme.secondary,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onConfirm()
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
