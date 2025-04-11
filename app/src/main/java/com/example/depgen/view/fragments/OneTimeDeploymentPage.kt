package com.example.depgen.view.fragments

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.NO_DATE
import com.example.depgen.model.EventComponent
import com.example.depgen.model.EventRole
import com.example.depgen.model.Navigation
import com.example.depgen.model.Profile
import com.example.depgen.utils.OTDCompleteSearchHelper
import com.example.depgen.utils.findRole
import com.example.depgen.view.components.CardButton
import com.example.depgen.view.components.ComboBox
import com.example.depgen.view.components.DefaultTopAppBar
import com.example.depgen.view.components.DisplayEventComponent
import com.example.depgen.view.components.QuantityPicker

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTimeDeploymentPage() {
    val roleNums = remember { mutableStateListOf<Int>() }
    val rolesNeeded = remember { mutableStateMapOf<EventRole, Int>() }
    val rolesNotSelected = remember { mutableStateListOf<String>() }
    var searchingRole by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("") }
    var roleError by remember { mutableStateOf("") }
    var recompile by remember { mutableIntStateOf(1) }
    var screen by remember { mutableStateOf("customise") }
    var generatedDeployment = remember { mutableStateMapOf<EventRole, List<Profile>>() }

    if (roleNums.isEmpty()) for (role in Global.rolesList) roleNums.add(role.minCount)

    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.ONETIMEDEPLOYMENT)
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                                    .height(500.dp)
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
                                            color = Color.Black
                                        )
                                    }
                                    Text(
                                        text = "Start typing to search for the role!",
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        ComboBox(
                                            rolesNotSelected,
                                            selectedRole,
                                            { selectedRole = it },
                                            errorMessage = roleError,
                                            resetErrorMessage = { roleError = "" }
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    CardButton(
                                        text = "Select",
                                        onClick = {
                                            val role = findRole(selectedRole)
                                            if (role == null) {
                                                roleError = "Undefined Role!"
                                            } else {
                                                selectedRole = ""
                                                rolesNeeded[role] = 1
                                                searchingRole = false
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
                        }
                    }
                    CardButton(
                        text = "Add Role Needed",
                        onClick = {
                            searchingRole = true
                            rolesNotSelected.clear()
                            for (role in Global.rolesList) {
                                if (!rolesNeeded.containsKey(role)) {
                                    rolesNotSelected.add(role.eventRole)
                                }
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CardButton(
                        text = "Generate!",
                        onClick = {
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
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
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

                    DisplayEventComponent(
                        component = component,
                        onEdit = null,
                        generateOTD = null
                    )
                }
            }
        }
    }
}