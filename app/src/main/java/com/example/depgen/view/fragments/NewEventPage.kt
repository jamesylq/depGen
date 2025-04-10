package com.example.depgen.view.fragments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.EVENT_TYPES
import com.example.depgen.Global
import com.example.depgen.model.Event
import com.example.depgen.model.EventComponent
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.save
import com.example.depgen.toast
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.EditEventComponent
import com.example.depgen.view.components.TimePickerScreen

fun removeLetters(str: String): String {
    var removed = ""
    for (char in str) {
        if (char in '0'..'9') {
            removed += char
        }
    }
    return removed
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventPage() {
    var name by remember { mutableStateOf("") }
    var picking by remember { mutableIntStateOf(0) }
    var eventComponent : EventComponent? by remember { mutableStateOf(null) }
    var addingEventComponent by remember { mutableStateOf(false) }
    var confirmationShowing by remember { mutableStateOf(false) }
    var editingEventComponent: EventComponent? by remember { mutableStateOf(null) }

    val selected = remember { mutableStateListOf<Boolean>() }
    if (selected.isEmpty()) for (i in EVENT_TYPES.indices) selected.add(false)

    val newEvent = remember { mutableStateOf(Event("", hashMapOf())) }
    var title by remember { mutableStateOf("") }

    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

     if (addingEventComponent) {
        EditEventComponent(
            eventComponent = eventComponent!!,
            onExit = {
                if (it != null) {
                    if (newEvent.value.components.containsKey(it)) {
                        newEvent.value.components[it]!!.add(eventComponent!!)
                    } else {
                        newEvent.value.components[it] = arrayListOf(eventComponent!!)
                    }
                }

                addingEventComponent = false
                eventComponent = null
            },
            comType = null
        )

    } else if (editingEventComponent != null) {
        val currType = newEvent.value.getComponentType(editingEventComponent!!)
        EditEventComponent(
            eventComponent = editingEventComponent!!,
            onExit = {
                if (it != currType && it != null) {
                    newEvent.value.components[currType]!!.remove(editingEventComponent!!)
                    if (newEvent.value.components[currType]!!.isEmpty()) {
                        newEvent.value.components.remove(currType)
                    }
                    if (!newEvent.value.components.containsKey(it)) {
                        newEvent.value.components[it] = ArrayList()
                    }
                    newEvent.value.components[it]!!.add(editingEventComponent!!)
                }
                editingEventComponent = null
            },
            comType = currType
        )

    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    confirmationShowing = true
                                },
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Close, "")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                fontSize = 24.sp,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        ) { innerPadding ->
            if (confirmationShowing) {
                ConfirmationScreen(
                    {
                        safeNavigate("EventList")
                        confirmationShowing = false
                    },
                    {
                        confirmationShowing = false
                    }
                )

            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (picking == 1) {
                    TimePickerScreen(
                        setPicking = { picking = it },
                        setText = { startTime = it },
                        "Select Starting Time"
                    )
                    return@Column

                } else if (picking == 2) {
                    TimePickerScreen(
                        setPicking = { picking = it },
                        setText = { endTime = it },
                        "Select Ending Time"
                    )
                    return@Column
                }

                title = "Create New Event"
                Text(
                    text = "Event Name",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        newEvent.value.name = name
                    },
                    placeholder = { Text("Enter Event Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Event Components",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 30.dp, bottom = 20.dp)
                )
                if (newEvent.value.components.isEmpty()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("No Components Yet!")
                    }

                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        item {
                            Column {
                                var rem = newEvent.value.components.size
                                for (entry in newEvent.value.components) {
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
                                            ),
                                            onClick = {
                                                editingEventComponent = component
                                            }
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
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                CardButton(
                    text = "Add Event Component",
                    onClick = {
                        eventComponent = EventComponent(HashMap(), HashMap(), "", "")
                        addingEventComponent = true
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                CardButton(
                    text = "Done",
                    onClick = {
                        if (name.isBlank()) {
                            toast("The Event Name cannot be empty!")
                        } else if (newEvent.value.components.isEmpty()) {
                            toast("There must be at least 1 Event Component!")
                        } else {
                            Global.eventList.add(newEvent.value)
                            safeNavigate("EventList")
                            save()
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}