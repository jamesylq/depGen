package com.example.depgen.view.fragments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.ComponentType
import com.example.depgen.model.EventComponent
import com.example.depgen.utils.copyToClipboard
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.DisplayEventComponent
import com.example.depgen.view.components.EditEventComponent
import com.example.depgen.view.components.GeneratingDeploymentScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPage(idx: Int) {
    val event = Global.eventList[idx]

    var generatingOTD: EventComponent? by remember { mutableStateOf(null) }
    var editingComponent: EventComponent? by remember { mutableStateOf(null) }
    var editingComponentType: ComponentType? by remember { mutableStateOf(null) }
    var addingComponent: EventComponent? by remember { mutableStateOf(null) }
    var deletingComponent: EventComponent? by remember { mutableStateOf(null) }

    val expanded = remember { mutableStateMapOf<EventComponent, Boolean>() }
    if (expanded.isEmpty()) {
        for (entry in event.getComponents()) {
            for (component in entry.value) {
                expanded[component] = false
            }
        }
    }

    BackHandler {
        safeNavigate("EventList")
    }

    Scaffold(
        topBar = {
            if (editingComponent == null && addingComponent == null) {
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
            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        copyToClipboard(event.toString())
                    },
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (Global.isAdmin()) {
                    if (editingComponent == null && addingComponent == null) {
                        FloatingActionButton(
                            onClick = {
                                addingComponent = EventComponent(HashMap(), HashMap(), "", "")
                            },
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
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

        } else if (addingComponent != null) {
            EditEventComponent(
                eventComponent = addingComponent!!,
                onExit = {
                    if (it != null) {
                        if (event.components.containsKey(it)) {
                            event.components[it]!!.add(addingComponent!!)
                        } else {
                            event.components[it] = arrayListOf(addingComponent!!)
                        }
                    }
                    addingComponent = null
                },
                comType = null
            )

        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (generatingOTD != null) {
                    GeneratingDeploymentScreen(generatingOTD!!) {
                        generatingOTD = null
                    }
                }

                if (deletingComponent != null) {
                    ConfirmationScreen(
                        onConfirm = {
                            for (entry in event.components) {
                                if (entry.value.contains(deletingComponent)) {
                                    entry.value.remove(deletingComponent)
                                    if (entry.value.isEmpty()) event.components.remove(entry.key)
                                    break
                                }
                            }
                            deletingComponent = null
                        },
                        onDecline = {
                            deletingComponent = null
                        },
                        body = "This action is irreversible!"
                    )
                }

                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                LazyColumn (
                    modifier = Modifier.weight(1f)
                ) {
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
                            if (Global.isAdmin()) {
                                for (component in entry.value) {
                                    if (!expanded.containsKey(component)) expanded[component] = false

                                    DisplayEventComponent(
                                        component = component,
                                        onEdit = {
                                            editingComponent = component
                                            editingComponentType = entry.key
                                        },
                                        generateOTD = {
                                            generatingOTD = component
                                        },
                                        expanded = expanded[component]!!,
                                        onToggleExpand = {
                                            expanded[component] = !expanded[component]!!
                                        },
                                        onDelete = {
                                            deletingComponent = component
                                        }
                                    )
                                    if (--rem > 0) Spacer(modifier = Modifier.height(15.dp))
                                }
                            } else {
                                for (component in entry.value) {
                                    DisplayEventComponent(
                                        component = component,
                                        expanded = expanded[component]!!,
                                        onToggleExpand = {
                                            expanded[component] = !expanded[component]!!
                                        }
                                    )
                                    if (--rem > 0) Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}