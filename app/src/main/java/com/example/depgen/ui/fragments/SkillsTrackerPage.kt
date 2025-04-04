package com.example.depgen.ui.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveRedEye
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.model.Navigation
import com.example.depgen.navController
import com.example.depgen.ui.components.DefaultTopAppBar

@Composable
fun SkillsTrackerPage() {
    Scaffold (
        topBar = {
            DefaultTopAppBar("Master", Navigation.SKILLSTRACKER)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("NewSkill")
            }) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
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
                for (i in Global.skillsList.indices) {
                    val skill = Global.skillsList[i]

                    item {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.Black
                            )
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
                                    IconButton(
                                        onClick = {
                                            navController.navigate("Skill/$i")
                                        },
                                        colors = IconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = Color.Black,
                                            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                                            disabledContentColor = Color.Black
                                        )
                                    ) {
                                        Icon(Icons.Default.RemoveRedEye, "")
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
                                        text = "1 to ${skill.maxLevel} (Default: ${skill.defaultLevel})",
                                        modifier = Modifier.padding(start = 5.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
