package com.example.depgen.view.fragments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.R
import com.example.depgen.model.ComponentType
import com.example.depgen.model.EventComponent
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.EditEventComponent
import com.example.depgen.view.components.EventRoleRender
import com.example.depgen.view.components.ExpandableBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventPage(idx: Int) {
    val event = Global.eventList[idx]

    var editingComponent: EventComponent? by remember { mutableStateOf(null) }
    var editingComponentType: ComponentType? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            if (editingComponent == null) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        titleContentColor = Color.Black
                    ),
                    title = {
                        Text(event.name)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            safeNavigate("EventList")
                        }) {
                            Icon(Icons.Default.Close, "")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (editingComponent == null) {
                FloatingActionButton(onClick = {
                    //TODO: Add Component
                }) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }
    ) { innerPadding ->
        if (editingComponent != null) {
            EditEventComponent(
                editingComponent!!,
                {
                    if (it != null && it != editingComponentType) {
                        val components = editingComponent!!.getEvent()!!.components
                        components[editingComponentType]!!.remove(editingComponent!!)
                        if (components[editingComponentType]!!.isEmpty()) {
                            components.remove(editingComponentType)
                        }
                        if (!components.containsKey(it)) {
                            components[it] = ArrayList()
                        }
                        components[it]!!.add(editingComponent!!)
                    }
                    editingComponent = null
                    editingComponentType = null
                },
                editingComponentType!!
            )

        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                LazyColumn {
                    item {
                        var rem = event.getComponents().size
                        for (entry in event.getComponents()) {
                            Text(
                                text = entry.key.componentType,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(
                                    bottom = 10.dp,
                                    start = 4.dp
                                )
                            )

                            entry.value.sortBy { it.getStart() }
                            for (component in entry.value) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize()
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
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                            ) {
                                                Spacer(modifier = Modifier.weight(1f))
                                                IconButton(
                                                    onClick = {
                                                        editingComponent = component
                                                        editingComponentType = entry.key
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Edit, "")
                                                }
                                            }
                                        }
                                        ExpandableBar("Members Deployed") {
                                            if (component.deployment.isEmpty()) {
                                                Text("No Members Deployed!")
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
                                            }
                                        }
                                    }
                                }
                                if (--rem > 0) Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}