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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.model.EventRole
import com.example.depgen.utils.toColor

@Composable
fun EventRoleRender(
    eventRole: EventRole,
    scale : Float = 1f,
    containerColor: Color = MaterialTheme.colorScheme.tertiary
) {
    ElevatedCard (
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = Color.Black
        ),
        modifier = Modifier.padding(top = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier
                .padding(vertical = 2.dp)
                .padding(start = 6.dp, end = 8.dp)
        ) {
            Canvas(modifier = Modifier.size((16 * scale).dp)) {
                drawCircle(
                    color = eventRole.color.toColor(),
                    radius = size.minDimension / 2
                )
            }
            Text(
                eventRole.eventRole,
                fontSize = (16 * scale).sp
            )
        }
    }
}
