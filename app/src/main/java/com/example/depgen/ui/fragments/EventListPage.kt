package com.example.depgen.ui.fragments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.ComponentType
import com.example.depgen.model.Event
import com.example.depgen.model.EventComponent
import com.example.depgen.model.Navigation
import com.example.depgen.navController
import com.example.depgen.save
import com.example.depgen.ui.components.CardButton
import com.example.depgen.ui.components.ConfirmationScreen
import com.example.depgen.ui.components.DefaultTopAppBar
import com.example.depgen.ui.components.EditEventComponent

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventListPage() {
    val eventRem = remember { mutableStateListOf<Event>() }
    var confirmationShowing by remember { mutableIntStateOf(-1) }
    var editingComponent: EventComponent? by remember { mutableStateOf(null) }
    var editingComponentType: ComponentType? by remember { mutableStateOf(null) }

    eventRem.clear()
    for (event in Global.eventList) eventRem.add(event)

    if (editingComponent != null) {
        EditEventComponent(
            editingComponent!!,
            {
                editingComponent = null
                editingComponentType = null
            },
            editingComponentType!!
        )

    } else {
        Scaffold(
            topBar = {
                DefaultTopAppBar("Master", Navigation.EVENTLIST)
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate("NewEvent")
                }) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        ) { innerPadding ->
            if (confirmationShowing > -1) {
                ConfirmationScreen(
                    {
                        eventRem.removeAt(confirmationShowing)
                        Global.eventList.removeAt(confirmationShowing)
                        save()
                        confirmationShowing = -1
                    },
                    {
                        confirmationShowing = -1
                    },
                    body = "This action is irreversible!"
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Event List",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0..< eventRem.size) {
                        item {
                            Text(
                                "Upcoming Event: ${eventRem[i].name}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 19.sp,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .padding(start = 8.dp)
                            )
                            ElevatedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .heightIn(50.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    var rem = eventRem[i].components.size
                                    for (entry in eventRem[i].components) {
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
//                                                    .height(7.dp)
                                                    .padding(bottom = 12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Row {
                                                        Column(
                                                            modifier = Modifier
                                                                .padding(vertical = 3.dp)
                                                                .padding(start = 8.dp)
                                                        ) {
                                                            Text(
                                                                text = "Start Time:",
                                                                modifier = Modifier.padding(bottom = 5.dp),
                                                                fontWeight = FontWeight.SemiBold
                                                            )
                                                            Text(
                                                                text = "  End Time:",
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
                                                        Row (
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier
                                                                .fillMaxHeight()
                                                        ) {
                                                            Spacer(modifier = Modifier.weight(1f))
                                                            IconButton(
                                                                onClick = {
                                                                    editingComponent = component
                                                                    editingComponentType = entry.key
    //                                                    navController.navigate("Event/${i}")
                                                                }
                                                            ) {
                                                                Icon(Icons.Default.Edit, "")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (--rem > 0) Spacer(modifier = Modifier.height(15.dp))
                                    }
                                    if (Global.isAdmin()) {
                                        CardButton(
                                            text = "Delete Event",
                                            onClick = {
                                                confirmationShowing = i
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    if (eventRem.isEmpty()) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Spacer(
                                    modifier = Modifier.height(
                                        maxOf(
                                            0,
                                            LocalConfiguration.current.screenHeightDp / 2 - 180
                                        ).dp
                                    )
                                )
                                Text(
                                    "No Upcoming Events!",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                Text("Time to take a well-deserved break!")
                            }
                        }
                    }
                }
            }
        }
    }
}