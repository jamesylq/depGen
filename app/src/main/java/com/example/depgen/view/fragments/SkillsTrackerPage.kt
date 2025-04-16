package com.example.depgen.view.fragments

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
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
import com.example.depgen.model.Navigation
import com.example.depgen.model.Skill
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.save
import com.example.depgen.view.components.ConfirmationScreen
import com.example.depgen.view.components.DefaultTopAppBar


@Composable
fun SkillsTrackerPage() {
    val remSkills = remember { mutableStateListOf<Skill>() }
    var deleting by remember { mutableIntStateOf(-1) }
    if (remSkills.isEmpty()) for (skill in Global.skillsList) remSkills.add(skill)

    BackHandler {
        safeNavigate("Master")
    }

    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.SKILLSTRACKER)
        },
        floatingActionButton = {
            if (Global.isAdmin()) {
                FloatingActionButton(
                    onClick = {
                        safeNavigate("NewSkill")
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
    ) {
        if (deleting != -1) {
            ConfirmationScreen(
                onConfirm = {
                    remSkills.removeAt(deleting)
                    Global.skillsList.removeAt(deleting)
                    deleting = -1
                    save()
                },
                onDecline = {
                    deleting = -1
                },
                body = "This action is irreversible!"
            )
        }
        Column (
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            Text(
                text = "Skills Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyColumn (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in remSkills.indices) {
                    val skill = remSkills[i]

                    item {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.Black
                            ),
                            onClick = {
                                safeNavigate("Skill/$i")
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Skill Name:",
                                        modifier = Modifier.padding(start = 5.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = skill.skill,
                                        modifier = Modifier.padding(start = 5.dp),
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (Global.isAdmin()) {
                                        IconButton(
                                            onClick = {
                                                deleting = i
                                            },
                                            colors = IconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary,
                                                contentColor = Color.Black,
                                                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                                                disabledContentColor = Color.Black
                                            )
                                        ) {
                                            Icon(Icons.Default.Delete, "")
                                        }
                                    }
                                }
                                Row (
                                    modifier = Modifier.padding(top = 12.dp, bottom = 15.dp)
                                ) {
                                    Text(
                                        text = "Available Skill Levels:",
                                        modifier = Modifier.padding(start = 5.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "0 to ${skill.maxLevel} (Default: ${skill.defaultLevel})",
                                        modifier = Modifier.padding(start = 5.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                if (remSkills.isEmpty()) {
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
                                "No Skills Found!",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            if (Global.isAdmin()) {
                                Text("Click \"+\" to create your first skill!")
                            } else {
                                Text("Check back when your admin creates skills!")
                            }
                        }
                    }
                }
            }
        }
    }
}
