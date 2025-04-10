package com.example.depgen.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.NO_DATE_OBJ
import com.example.depgen.R
import com.example.depgen.model.EventComponent

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplayEventComponent(component: EventComponent, onEdit: (() -> Unit)? = null, generateOTD: (() -> Unit)? = null) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Event Details",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.SemiBold
            )
            Row {
                Column(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = "Start:",
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "  End:",
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .padding(start = 5.dp)
                ) {
                    if (component.getStart() == NO_DATE_OBJ) {
                        for (x in 0..1) {
                            Text(
                                text = "N/A",
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }
                    } else {
                        Text(
                            text = component.getNaturalStart(),
                            modifier = Modifier.padding(
                                bottom = 5.dp
                            )
                        )
                        Text(
                            text = component.getNaturalEnd(),
                            modifier = Modifier.padding(
                                bottom = 5.dp
                            )
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            if (onEdit != null) {
                                onEdit()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Edit, "")
                    }
                }
            }
            ExpandableBar("Members Deployed", true) {
                if (component.deployment.isEmpty()) {
                    Text("No Members Deployed Yet!")
                    Spacer(modifier = Modifier.height(10.dp))
                    CardButton(
                        text = "Auto Generate Deployment",
                        onClick = {
                            if (generateOTD != null) {
                                generateOTD()
                            }
                        }
                    )
                }
                for (deploymentEntry in component.deployment) {
                    ElevatedCard (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Image(
                                //TODO: Profile Picture
                                painter = painterResource(R.drawable.icon_512),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(13.dp))
                            )
                            Column {
                                Text(
                                    text = deploymentEntry.key.username,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                FlowRow (
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(start = 5.dp)
                                ) {
                                    for (role in deploymentEntry.value) {
                                        EventRoleRender(role, 0.8f)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}