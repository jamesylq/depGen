package com.example.depgen.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.luxuryManager
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplayProfileDeployment(profile: Profile, roles: ArrayList<EventRole>) {
    ElevatedCard (
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Row (
            modifier = Modifier.padding(10.dp)
        ) {
            luxuryManager.getLuxury(profile).ProfilePicture(
                clip = RoundedCornerShape(13.dp),
                size = 60.dp
            )
            Column {
                Text(
                    text = profile.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                FlowRow (
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    for (role in roles) {
                        EventRoleRender(role, 0.8f)
                    }
                }
            }
        }
    }
}