package com.example.depgen.view.fragments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depgen.Global
import com.example.depgen.luxuryManager
import com.example.depgen.model.Navigation
import com.example.depgen.model.Skill
import com.example.depgen.toast
import com.example.depgen.utils.safeNavigate
import com.example.depgen.utils.save
import com.example.depgen.view.components.DefaultTopAppBar
import com.example.depgen.view.components.QuantityPicker

fun refreshSkillsSorted(sortedMembers: MutableList<Pair<Int, Int>>, skill: Skill) {
    sortedMembers.clear()
    for (i in 2..< Global.profileList.size) {
        val profile = Global.profileList[i]
        if (!profile.skills.containsKey(skill)) {
            profile.skills[skill] = skill.defaultLevel
        }
        sortedMembers.add(Pair(profile.skills[skill]!!, i))
    }
    sortedMembers.sortByDescending { it.first }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillPage(idx: Int) {
    val skill = Global.skillsList[idx]
    var confirmation by remember { mutableIntStateOf(-1) }
    val sortedMembers = remember { mutableListOf<Pair<Int, Int>>() }

    if (sortedMembers.isEmpty()) {
        refreshSkillsSorted(sortedMembers, skill)
    }

    BackHandler {
        safeNavigate("SkillsTracker")
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar("SkillsTracker", Navigation.SKILL)
        }
    ) { innerPadding ->
        if (confirmation != -1) {
            val profile = Global.profileList[sortedMembers[confirmation].second]
            var newLvl by remember { mutableIntStateOf(profile.skills[skill]!!) }

            BasicAlertDialog(
                onDismissRequest = {
                    confirmation = -1
                }
            ) {
                ElevatedCard (
                    modifier = Modifier.height(320.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onTertiary,
                        contentColor = Color.Black
                    )
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Edit Skill Level", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("(${profile.username})", fontSize = 17.sp, modifier = Modifier.padding(top = 3.dp))
                        Spacer(modifier = Modifier.height(60.dp))
                        QuantityPicker(
                            { newLvl = it },
                            profile.skills[skill]!!,
                            maxQty = skill.maxLevel
                        )
                        Spacer(modifier = Modifier.height(60.dp))
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    confirmation = -1
                                },
                                modifier = Modifier.padding(end=60.dp),
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = Color.Black,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondary,
                                    disabledContentColor = Color.Black
                                )
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    confirmation = -1
                                    profile.skills[skill] = newLvl
                                    refreshSkillsSorted(sortedMembers, skill)
                                    save()
                                },
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.Black,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = Color.Black
                                )
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Information for Skill \"${skill.skill}\"",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    if (sortedMembers.isNotEmpty()) {
                        for (i in 0 ..< sortedMembers.size) {
                            val profile = Global.profileList[sortedMembers[i].second]

                            item {
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(114.dp)
                                        .padding(vertical = 7.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.onTertiary,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Column {
                                        Row {
                                            luxuryManager.getLuxury(profile).ProfilePicture(
                                                clip = RoundedCornerShape(10.dp),
                                                size = 100.dp
                                            )
                                            Text(
                                                profile.username,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 19.sp,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Column (
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clickable {
                                                        if (Global.isAdmin()) {
                                                            confirmation = i
                                                        } else {
                                                            toast("You must be an admin to do this!")
                                                        }
                                                    },
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = profile.skills[skill]!!.toString(),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 30.sp
                                                )
                                                Text("Skill Level")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.height(100.dp))
                                Text(
                                    text = "No Member Found!",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
