package com.example.depgen.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.EventComponent
import com.example.depgen.model.LuxuryManager
import com.example.depgen.utils.NO_DATE_OBJ

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplayEventComponent(
    component: EventComponent,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: (() -> Unit)? = null,
    generateOTD: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
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
            Row {
                Text(
                    text = "Event Details",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.SemiBold
                )
                if (onDelete != null && Global.isAdmin()) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "")
                    }
                }
            }
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

                    if (onEdit != null) {
                        IconButton(
                            onClick = onEdit
                        ) {
                            Icon(Icons.Default.Edit, "")
                        }
                    }
                }
            }
            ExpandableBar(
                "Members Deployed",
                expanded,
                onToggleExpand
            ) {
                if (component.deployment.isEmpty()) {
                    Text("No Members Deployed Yet!")
                    Spacer(modifier = Modifier.height(10.dp))

                    if (generateOTD != null) {
                        CardButton(
                            text = "Auto Generate Deployment",
                            onClick = generateOTD
                        )
                    }
                }
                for (deploymentEntry in component.deployment) {
                    ElevatedCard (
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row (
                            modifier = Modifier.padding(10.dp)
                        ) {
                            LuxuryManager.getLuxury(deploymentEntry.key).ProfilePicture(
                                clip = RoundedCornerShape(13.dp),
                                size = 60.dp
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