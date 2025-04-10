package com.example.depgen.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.utils.listToColor
import com.example.depgen.model.EventRole

@Composable
fun EventRoleRender(eventRole: EventRole, scale : Float = 1f) {
    ElevatedCard (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = Color.Black
        ),
        modifier = Modifier.scale(scale)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(vertical = 5.dp).padding(start = 4.dp, end = 8.dp)
        ){
            Canvas(modifier = Modifier.size(16.dp)) {
                drawCircle(
                    color = listToColor(eventRole.color),
                    radius = size.minDimension / 2
                )
            }
            Text(
                eventRole.eventRole,
                fontSize = 16.sp
            )
        }
    }
}
