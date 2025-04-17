package com.example.depgen.view.fragments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.Profile
import com.example.depgen.utils.NO_DATE
import com.example.depgen.utils.OTDCompleteSearchHelper
import com.example.depgen.utils.copyToClipboard
import com.example.depgen.utils.findRole
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.DisplayEventComponent
import com.example.depgen.view.components.MultiSelectComboBox
import com.example.depgen.view.components.QuantityPicker


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTimeDeploymentPage() {
    val roles = remember { Global.rolesList.map { it.eventRole }.toMutableStateList() }
    val roleNums = remember { mutableStateListOf<Int>() }
    val rolesNeeded = remember { mutableStateMapOf<EventRole, Int>() }
    var confirmationShowing by remember { mutableStateOf(false) }
    var searchingRole by remember { mutableStateOf(false) }
    var selectedRoles by remember { mutableStateOf(setOf<String>()) }
    var recompile by remember { mutableIntStateOf(1) }
    var screen by remember { mutableStateOf("customise") }
    val generatedDeployment = remember { mutableStateMapOf<EventRole, List<Profile>>() }
    var expanded by remember { mutableStateOf(true) }

    if (roleNums.isEmpty()) for (role in Global.rolesList) roleNums.add(role.minCount)

    BackHandler {
        when (screen) {
            "customise", "generated" -> {
                confirmationShowing = true
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                when (screen) {
                                    "customise", "generated" -> {
                                        confirmationShowing = true
                                    }
                                    else -> {}
                                }
                            },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(
                                imageVector = when (screen) {
                                    "generated", "customise" -> Icons.Default.Close
                                    else -> Icons.Default.QuestionMark
                                },
                                contentDescription = ""
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "One-time Deployment",
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
        LaunchedEffect (screen) {
            if (screen == "regenerate") {
                OTDCompleteSearchHelper(
                    EventComponent(
                        HashMap(),
                        HashMap(rolesNeeded),
                        NO_DATE,
                        NO_DATE
                    )
                ).generate().forEach {
                    generatedDeployment[it.key] = it.value
                }
                screen = "generated"
            }
        }

        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (confirmationShowing) {
                ConfirmationScreen(
                    {
                        confirmationShowing = false
                        safeNavigate("Master")
                    },
                    {
                        confirmationShowing = false
                    }
                )
            }

            when (screen) {
                "customise" -> {
                    if (searchingRole) {
                        BasicAlertDialog(
                            onDismissRequest = {
                                searchingRole = false
                            }
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Select Role",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    Text(
                                        text = "Start typing to search for the role!",
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    MultiSelectComboBox(
                                        roles,
                                        selectedRoles,
                                        {
                                            if (selectedRoles.contains(it)) {
                                                selectedRoles -= it
                                                rolesNeeded.remove(findRole(it))
                                            } else {
                                                selectedRoles += it
                                                rolesNeeded[findRole(it)!!] = 1
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.weight(1f))
                                    CardButton(
                                        text = "Select",
                                        onClick = {
                                            searchingRole = false
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Deployment",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Select the roles needed for your event!",
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (recompile > 0) {
                            for (entry in rolesNeeded) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(entry.key.eventRole)
                                        Spacer(modifier = Modifier.weight(1f))
                                        QuantityPicker(
                                            {
                                                rolesNeeded[entry.key] = it
                                                if (rolesNeeded[entry.key] == 0) {
                                                    rolesNeeded.remove(entry.key)
                                                    selectedRoles -= entry.key.eventRole
                                                    recompile++
                                                }
                                            },
                                            initialQty = minOf(
                                                maxOf(
                                                    entry.key.minCount,
                                                    rolesNeeded[entry.key]!!
                                                ), entry.key.maxCount
                                            ),
                                            minQty = entry.key.minCount,
                                            maxQty = entry.key.maxCount,
                                            scale = 0.8f
                                        )
                                    }
                                }
                            }

                            if (rolesNeeded.isEmpty()) {
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
                                            "No Roles Yet!",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        )
                                        Text("Add the roles required using the button below!")
                                    }
                                }
                            }
                        }
                    }

                    CardButton(
                        text = "Add Role Needed",
                        onClick = {
                            searchingRole = true
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Generate!",
                        onClick = {
                            screen = "regenerate"
                        },
                        enabled = rolesNeeded.isNotEmpty()
                    )
                }

                "generated" -> {
                    val component = EventComponent(
                        HashMap(),
                        HashMap(rolesNeeded),
                        NO_DATE,
                        NO_DATE
                    )
                    component.setDeployment(HashMap(generatedDeployment))
                    Column {
                        LazyColumn (
                            modifier = Modifier.weight(1f)
                        ) {
                            item {
                                DisplayEventComponent(
                                    component = component,
                                    onEdit = null,
                                    generateOTD = null,
                                    expanded = expanded,
                                    onToggleExpand = { expanded = !expanded }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        CardButton(
                            text = "Regenerate!",
                            onClick = {
                                screen = "regenerate"
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CardButton(
                            text = "Copy Deployment to Clipboard",
                            onClick = {
                                copyToClipboard(component.toString(false))
                            }
                        )
                    }
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(
                            modifier = Modifier.height(
                                maxOf(
                                    0,
                                    LocalConfiguration.current.screenHeightDp / 2
                                ).dp
                            )
                        )
                        Text(
                            "Generating...",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text("Please give us a few seconds!")
                    }
                }
            }
        }
    }
}
