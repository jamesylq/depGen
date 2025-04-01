package com.example.depgen.ui.fragments

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.model.Event
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.navController
import com.example.depgen.ui.components.DefaultTopAppBar
import com.example.depgen.ui.theme.CARD_ORANGE

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventListPage() {
    val eventRem = remember { mutableStateListOf<Event>() }

    eventRem.clear()
    for (event in Global.eventList) {
        eventRem.add(event)
        Log.d("COMPONENT SIZE", "${event.components.size}")
    }

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
        Column (
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
            LazyColumn {
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
                        ElevatedCard (
                            colors = CardDefaults.cardColors(
                                containerColor = CARD_ORANGE
                            ),
                            onClick = {
                                navController.navigate("Event/${i}")
                            }
                        ) {
                            Column (
                                modifier = Modifier.padding(8.dp)
                            ) {
                                var rem = eventRem[i].components.size
                                for (entry in eventRem[i].components) {
                                    Text(
                                        text = entry.key.componentType,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                    entry.value.sortBy { it.getStart() }
                                    for (component in entry.value) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp)
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
                                                            .padding(vertical = 10.dp)
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
                                                            .padding(vertical = 10.dp)
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
                                                }
                                            }
                                        }
                                    }
                                    if (--rem > 0) Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}