package com.example.depgen.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardButton(
    text: String,
    onClick: () -> Unit,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.onTertiary
    ),
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    ElevatedCard(
        colors = colors,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        enabled = enabled
    ) {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(7.dp))
            }
            Text(
                text = text,
                color = Color.Black
            )
        }
    }
}
