package com.example.depgen.view.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.depgen.model.EventRole
import com.example.depgen.model.Navigation
import com.example.depgen.utils.safeNavigate
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.DefaultTopAppBar
import com.example.depgen.view.components.EventRoleRender


@Composable
fun RolesListPage() {
    val remRolesList = remember { mutableStateListOf<EventRole>() }
    var deleting by remember { mutableIntStateOf(-1) }
    if (remRolesList.isEmpty()) for (role in Global.rolesList) remRolesList.add(role)

    Scaffold(
        topBar = {
            DefaultTopAppBar("Master", Navigation.EVENTLIST)
        },
        floatingActionButton = {
            if (Global.isAdmin()) {
                FloatingActionButton(onClick = {
                    safeNavigate("NewRole/-1")
                }) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        }
    ) {
        if (deleting != -1) {
            ConfirmationScreen(
                onConfirm = {
                    remRolesList.removeAt(deleting)
                    Global.rolesList.removeAt(deleting)
                    deleting = -1
                },
                onDecline = {
                    deleting = -1
                },
                body = "This action is irreversible!"
            )
        }
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            Text(
                text = "List of Roles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            LazyColumn {
                for (i in remRolesList.indices) {
                    val role = remRolesList[i]
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.Black
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Role: ", fontWeight = FontWeight.Bold)
                                            EventRoleRender(role)
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (Global.isAdmin()) {
                                        IconButton(
                                            onClick = { deleting = i }
                                        ) {
                                            Icon(Icons.Default.Delete, "")
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Prerequisites:",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 5.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (Global.isAdmin()) {
                                        IconButton(
                                            onClick = {
                                                safeNavigate("NewRole/$i")
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, "")
                                        }
                                    }
                                }
                                for (entry in role.prerequisites) {
                                    ElevatedCard {
                                        Column(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${entry.key.skill} Skill",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            for (j in entry.value.indices) {
                                                DisplayRequirement(
                                                    entry.value,
                                                    j,
                                                    deleteShowing = false
                                                )
                                            }
                                        }
                                    }
                                }
                                if (role.prerequisites.isEmpty()) {
                                    ElevatedCard {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                        ) {
                                            Text("This role has no prerequisites!")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (remRolesList.isEmpty()) {
                Column (
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
                        "No Roles Found!",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text("Click \"+\" to create your first role!")
                }
            }
        }
    }
}